package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.CustomerRecord
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyPrimary
import java.io.File

@Composable
fun BillSlipDialog(
    record: CustomerRecord,
    lang: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .clip(RoundedCornerShape(16.dp))
                .testTag("bill_slip_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldGreen
                        ) {
                            Text(
                                text = "⚡ EV",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.get(lang, "bill_header"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_bill_dialog_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 6.dp))

                // Scrollable Invoice Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .border(
                            border = BorderStroke(1.5.dp, EmeraldGreen.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White)
                        .padding(14.dp)
                ) {
                    // Agency Header
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "MS HUSAINI ENTERPRISES",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoyalNavyDark,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Authorized Electric E-Rickshaw & EV Showroom",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldGreen
                        )
                        Text(
                            text = "Proprietors: Mohammad Anees • Mohammad Suhail",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = "📞 7668732106, 7906599049",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldGreen,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = "🛺 ELECTRIC E-RICKSHAW DELIVERY & FINANCE SLIP",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Meta: Invoice & Date
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFF0FDF4),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Inv: ${record.invoiceNo}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                        Text(
                            text = "Delivery Date: ${record.purchaseDate}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Data Grid
                    InvoiceFieldRow("Customer Name", record.custName, isBold = true)
                    InvoiceFieldRow("Mobile No", record.custPhone)
                    InvoiceFieldRow("Aadhaar No", record.custAadhaar)
                    InvoiceFieldRow("E-Rickshaw Model", "🛺 " + record.vehModel, isBold = true, highlight = true)
                    InvoiceFieldRow("Vehicle / Temp No", record.vehNo, isBold = true)
                    if (record.motorChassisNo.isNotBlank()) {
                        InvoiceFieldRow("Motor & Chassis No", record.motorChassisNo)
                    }
                    if (record.batteryDetails.isNotBlank()) {
                        InvoiceFieldRow("Battery & Guarantee", "🔋 " + record.batteryDetails, highlight = true)
                    }
                    if (record.chargerDetails.isNotBlank()) {
                        InvoiceFieldRow("EV Smart Charger", record.chargerDetails)
                    }
                    InvoiceFieldRow("Total Price (कुल कीमत)", PrintHelper.formatCurrency(record.totalPrice), highlight = true)
                    InvoiceFieldRow("Finance Amount", PrintHelper.formatCurrency(record.finAmount))
                    InvoiceFieldRow("Down Payment", PrintHelper.formatCurrency(record.downPayment))
                    InvoiceFieldRow("Kist (EMI Details)", record.kistDetails, highlight = true)
                    InvoiceFieldRow("Showroom / Source", record.vehSource)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Photos Row (Customer, Vehicle Card, Battery Card)
                    val hasPhotos = record.custPhotoPath != null || record.warPhotoPath != null || record.batteryCardPhotoPath != null
                    if (hasPhotos) {
                        Text(
                            text = "Attached Documents & Cards:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            record.custPhotoPath?.let { path ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Customer", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    AsyncImage(
                                        model = File(path),
                                        contentDescription = "Customer Photo",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            record.warPhotoPath?.let { path ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Vehicle Card", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    AsyncImage(
                                        model = File(path),
                                        contentDescription = "Vehicle Warranty",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            record.batteryCardPhotoPath?.let { path ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Battery Card", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    AsyncImage(
                                        model = File(path),
                                        contentDescription = "Battery Guarantee",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Signatures
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(1.dp)
                                    .background(Color.Gray)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Customer Sign",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(1.dp)
                                    .background(Color.Gray)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Authorized E-Rickshaw Dealer",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Ms Husaini Enterprises",
                                fontSize = 9.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            PrintHelper.shareViaWhatsApp(context, record, lang)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("whatsapp_share_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "WhatsApp", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "WhatsApp", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            PrintHelper.printInvoice(context, record, lang)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("print_pdf_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.get(lang, "btn_print_pdf"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceFieldRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (highlight) Color(0xFFF0FDF4) else Color.Transparent)
            .border(0.5.dp, Color(0xFFE2E8F0))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (highlight) EmeraldGreen else Color(0xFF475569),
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.weight(0.42f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = if (highlight) EmeraldGreen else Color(0xFF0F172A),
            fontWeight = if (isBold || highlight) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.58f)
        )
    }
}
