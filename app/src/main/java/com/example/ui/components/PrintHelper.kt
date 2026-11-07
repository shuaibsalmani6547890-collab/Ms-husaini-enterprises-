package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.CustomerRecord
import com.example.model.AppLanguage
import java.text.NumberFormat
import java.util.Locale

object PrintHelper {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun formatCurrency(amount: Double): String {
        return try {
            currencyFormat.format(amount).replace("₹", "₹ ")
        } catch (e: Exception) {
            "₹ $amount"
        }
    }

    fun printInvoice(context: Context, record: CustomerRecord, lang: AppLanguage) {
        val htmlContent = generateInvoiceHtml(record, lang)

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                val printAdapter = webView.createPrintDocumentAdapter("Invoice_${record.invoiceNo}")
                val jobName = "Husaini_ERickshaw_Invoice_${record.invoiceNo}"
                printManager?.print(jobName, printAdapter, PrintAttributes.Builder().build())
            }
        }

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun generateWhatsAppText(record: CustomerRecord, lang: AppLanguage): String {
        return buildString {
            appendLine("🛺 *MS HUSAINI ENTERPRISES*")
            appendLine("⚡ *Electric E-Rickshaw & EV Delivery Slip*")
            appendLine("👤 Mohammad Anees | Mohammad Suhail")
            appendLine("📞 7668732106, 7906599049")
            appendLine("--------------------------------")
            appendLine("📄 *Invoice No:* ${record.invoiceNo}")
            appendLine("📅 *Date:* ${record.purchaseDate}")
            appendLine("👤 *Customer:* ${record.custName}")
            appendLine("📱 *Phone:* ${record.custPhone}")
            appendLine("🆔 *Aadhaar:* ${record.custAadhaar}")
            appendLine("🛺 *E-Rickshaw Model:* ${record.vehModel}")
            appendLine("🔢 *Vehicle/Temp No:* ${record.vehNo}")
            if (record.motorChassisNo.isNotBlank()) {
                appendLine("⚙️ *Chassis/Motor:* ${record.motorChassisNo}")
            }
            if (record.batteryDetails.isNotBlank()) {
                appendLine("🔋 *Battery & Guarantee:* ${record.batteryDetails}")
            }
            if (record.chargerDetails.isNotBlank()) {
                appendLine("🔌 *Charger:* ${record.chargerDetails}")
            }
            appendLine("💰 *Total Price:* ${formatCurrency(record.totalPrice)}")
            appendLine("💳 *Finance Amount:* ${formatCurrency(record.finAmount)}")
            appendLine("💵 *Down Payment:* ${formatCurrency(record.downPayment)}")
            appendLine("📅 *Kist (EMI):* ${record.kistDetails}")
            appendLine("🏢 *Purchased From:* ${record.vehSource}")
            appendLine("--------------------------------")
            appendLine("Congratulations on your new Electric E-Rickshaw! 🙏")
        }
    }

    fun shareViaWhatsApp(context: Context, record: CustomerRecord, lang: AppLanguage) {
        val message = generateWhatsAppText(record, lang)
        val cleanPhone = record.custPhone.filter { it.isDigit() }
        val targetPhone = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?phone=$targetPhone&text=" + Uri.encode(message))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val genericIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(genericIntent, "Share E-Rickshaw Bill via"))
        }
    }

    fun generateInvoiceHtml(record: CustomerRecord, lang: AppLanguage): String {
        val isHindi = lang == AppLanguage.HINDI
        val titleText = if (isHindi) "मेसर्स हुसैनी एंटरप्राइजेज" else "MS HUSAINI ENTERPRISES"
        val subtitle = if (isHindi) "इलेक्ट्रिक ई-रिक्शा डिलीवरी एवं फाइनेंस बिल पर्ची" else "ELECTRIC E-RICKSHAW DELIVERY & FINANCE INVOICE"
        val ownersText = if (isHindi) "प्रोप्राइटर: मोहम्मद अनीस एवं मोहम्मद सुहैल" else "Proprietors: Mohammad Anees & Mohammad Suhail"

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <style>
                body {
                    font-family: 'Segoe UI', Arial, sans-serif;
                    margin: 20px;
                    color: #0f172a;
                    background: #ffffff;
                }
                .invoice-box {
                    max-width: 800px;
                    margin: auto;
                    padding: 24px;
                    border: 2px solid #059669;
                    border-radius: 10px;
                }
                .header {
                    text-align: center;
                    border-bottom: 2px solid #059669;
                    padding-bottom: 12px;
                    margin-bottom: 16px;
                }
                .header h1 {
                    margin: 0;
                    color: #059669;
                    font-size: 26px;
                    text-transform: uppercase;
                    letter-spacing: 1px;
                }
                .header .sub {
                    font-size: 13px;
                    color: #334155;
                    margin: 4px 0;
                    font-weight: 600;
                }
                .header .contacts {
                    font-size: 13px;
                    color: #0f172a;
                    font-weight: bold;
                }
                .invoice-badge {
                    display: inline-block;
                    background: #059669;
                    color: #ffffff;
                    padding: 4px 14px;
                    border-radius: 4px;
                    font-size: 13px;
                    font-weight: bold;
                    margin-top: 6px;
                }
                .meta-table {
                    width: 100%;
                    margin-bottom: 12px;
                }
                .meta-table td {
                    padding: 4px 0;
                    font-size: 14px;
                }
                .content-table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 8px;
                }
                .content-table th, .content-table td {
                    border: 1px solid #cbd5e1;
                    padding: 8px 12px;
                    text-align: left;
                    font-size: 13px;
                }
                .content-table th {
                    background: #f0fdf4;
                    color: #065f46;
                    font-weight: 700;
                    width: 38%;
                }
                .highlight-row {
                    background: #ecfdf5;
                    font-weight: bold;
                    color: #047857;
                }
                .signatures {
                    margin-top: 45px;
                    display: flex;
                    justify-content: space-between;
                }
                .sig-block {
                    text-align: center;
                    width: 220px;
                    border-top: 1px dashed #475569;
                    padding-top: 8px;
                    font-size: 13px;
                    font-weight: 600;
                }
                .footer {
                    text-align: center;
                    margin-top: 20px;
                    font-size: 11px;
                    color: #64748b;
                    border-top: 1px solid #e2e8f0;
                    padding-top: 8px;
                }
            </style>
        </head>
        <body>
            <div class="invoice-box">
                <div class="header">
                    <h1>$titleText</h1>
                    <div class="sub">$ownersText</div>
                    <div class="contacts">📞 7668732106, 7906599049</div>
                    <div class="invoice-badge">⚡ $subtitle</div>
                </div>

                <table class="meta-table">
                    <tr>
                        <td><b>Invoice No:</b> ${record.invoiceNo}</td>
                        <td style="text-align: right;"><b>Delivery Date:</b> ${record.purchaseDate}</td>
                    </tr>
                </table>

                <table class="content-table">
                    <tr>
                        <th>Customer Name</th>
                        <td><b>${record.custName}</b></td>
                    </tr>
                    <tr>
                        <th>Mobile Number</th>
                        <td>${record.custPhone}</td>
                    </tr>
                    <tr>
                        <th>Aadhaar Number</th>
                        <td>${record.custAadhaar}</td>
                    </tr>
                    <tr class="highlight-row">
                        <th>E-Rickshaw Model</th>
                        <td><b>🛺 ${record.vehModel}</b></td>
                    </tr>
                    <tr>
                        <th>Vehicle / Temp Number</th>
                        <td><b>${record.vehNo}</b></td>
                    </tr>
                    <tr>
                        <th>Chassis & Motor No</th>
                        <td>${record.motorChassisNo.ifBlank { "Verified OK" }}</td>
                    </tr>
                    <tr class="highlight-row">
                        <th>Battery Details & Guarantee</th>
                        <td>🔋 ${record.batteryDetails.ifBlank { "Standard EV Battery" }}</td>
                    </tr>
                    <tr>
                        <th>EV Smart Charger</th>
                        <td>${record.chargerDetails.ifBlank { "Included" }}</td>
                    </tr>
                    <tr class="highlight-row">
                        <th>Total Price (कुल कीमत)</th>
                        <td>${formatCurrency(record.totalPrice)}</td>
                    </tr>
                    <tr>
                        <th>Finance Amount</th>
                        <td>${formatCurrency(record.finAmount)}</td>
                    </tr>
                    <tr>
                        <th>Down Payment</th>
                        <td>${formatCurrency(record.downPayment)}</td>
                    </tr>
                    <tr class="highlight-row">
                        <th>Installment (Kist Details)</th>
                        <td>${record.kistDetails}</td>
                    </tr>
                    <tr>
                        <th>Showroom / Agency</th>
                        <td>${record.vehSource}</td>
                    </tr>
                </table>

                <div class="signatures">
                    <div class="sig-block">
                        Customer Signature<br>
                        <span style="font-size: 11px; color:#64748b;">(ग्राहक के हस्ताक्षर)</span>
                    </div>
                    <div class="sig-block">
                        For Ms Husaini Enterprises<br>
                        <span style="font-size: 11px; color:#64748b;">(Authorized E-Rickshaw Dealer)</span>
                    </div>
                </div>

                <div class="footer">
                    Ms Husaini Enterprises • Authorized Electric E-Rickshaw Dealership • Mohammad Anees & Mohammad Suhail • Ph: 7668732106, 7906599049
                </div>
            </div>
        </body>
        </html>
        """.trimIndent()
    }
}
