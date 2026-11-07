package com.example.model

enum class AppLanguage {
    HINDI,
    ENGLISH
}

object AppStrings {
    fun get(lang: AppLanguage, key: String): String {
        val dict = when (lang) {
            AppLanguage.HINDI -> hindiStrings
            AppLanguage.ENGLISH -> englishStrings
        }
        return dict[key] ?: key
    }

    private val englishStrings = mapOf(
        "app_title" to "Ms Husaini Enterprises",
        "agency_sub" to "Authorized Electric E-Rickshaw & EV Dealership",
        "owners" to "Mohammad Anees & Mohammad Suhail",
        "nav_new_bill" to "New EV Bill",
        "nav_customers" to "EV Customers",
        "nav_sync" to "Sync Phones",
        "nav_agency" to "Agency Info",
        
        // Form Fields
        "cust_name" to "Customer Name",
        "cust_phone" to "Mobile Number",
        "cust_aadhaar" to "Aadhaar Card Number",
        "veh_model" to "E-Rickshaw Model / Type",
        "veh_no" to "Vehicle Reg / Temp Number",
        "purchase_date" to "Purchase Date",
        "total_price" to "Total Price (₹)",
        "fin_amount" to "Finance Amount (₹)",
        "kist_details" to "Installment Details (EMI)",
        "veh_source" to "Purchased From (Agency / Source)",
        "down_payment" to "Down Payment (Calculated)",
        "battery_details" to "Battery Type & Guarantee Details",
        "motor_chassis" to "Motor & Chassis Number",
        "charger_details" to "EV Smart Charger Details",
        "cust_photo" to "Customer Photo",
        "warranty_photo" to "E-Rickshaw Guarantee Card",
        "battery_card_photo" to "Battery Guarantee Card Photo",
        
        // Actions
        "btn_generate_bill" to "1-Click Generate EV Bill & Save",
        "btn_save_changes" to "Save Changes",
        "btn_search_placeholder" to "Search Name, Phone, Aadhaar, Chassis, or E-Rickshaw...",
        "btn_share_whatsapp" to "Share on WhatsApp",
        "btn_print_pdf" to "Print / Save PDF Bill",
        "btn_call" to "Call Customer",
        "btn_edit" to "Edit Record",
        "btn_delete" to "Delete Record",
        "btn_export_2nd_phone" to "Export Data for 2nd Phone",
        "btn_import_2nd_phone" to "Import Data from 2nd Phone",
        "btn_take_photo" to "Take Photo with Camera",
        "btn_choose_gallery" to "Choose from Gallery",
        "btn_remove_photo" to "Remove Photo",
        
        // Headers & Labels
        "bill_header" to "ELECTRIC E-RICKSHAW DELIVERY & FINANCE INVOICE",
        "invoice_no" to "Invoice No",
        "cust_sign" to "Customer Signature",
        "auth_sign" to "Authorized Signatory (Ms Husaini Enterprises)",
        "total_records" to "Total E-Rickshaws Sold",
        "total_sales" to "Total Sales Value",
        "total_financed" to "Total Financed",
        "no_records" to "No E-Rickshaw customer records found",
        "sync_help_title" to "Dual-Phone Sync Guide",
        "sync_help_desc" to "Sync all E-Rickshaw delivery data between Mohammad Anees and Mohammad Suhail phones in 1 click.",
        "cloud_sync_url" to "Google Sheet / Webhook Sync URL (Optional)",
        "cloud_sync_btn" to "Sync with Google Sheet",
        "success_saved" to "E-Rickshaw bill saved & generated successfully!",
        "success_imported" to "Records imported successfully from 2nd phone!",
        "confirm_delete" to "Are you sure you want to delete this customer record?",
        "cancel" to "Cancel",
        "delete" to "Delete",
        "close" to "Close"
    )

    private val hindiStrings = mapOf(
        "app_title" to "मेसर्स हुसैनी एंटरप्राइजेज",
        "agency_sub" to "ऑथराइज्ड ई-रिक्शा एवं इलेक्ट्रिक ईवी शोरूम",
        "owners" to "मोहम्मद अनीस एवं मोहम्मद सुहैल",
        "nav_new_bill" to "नया ई-रिक्शा बिल",
        "nav_customers" to "ग्राहक रिकॉर्ड",
        "nav_sync" to "दूसरे फ़ोन में सिंक",
        "nav_agency" to "एजेंसी जानकारी",
        
        // Form Fields
        "cust_name" to "ग्राहक का नाम (Customer Name)",
        "cust_phone" to "मोबाइल नंबर (Mobile No)",
        "cust_aadhaar" to "आधार कार्ड नंबर (Aadhaar No)",
        "veh_model" to "कौनसा ई-रिक्शा है (E-Rickshaw Model)",
        "veh_no" to "गाड़ी / अस्थायी नंबर (Vehicle/Temp No)",
        "purchase_date" to "कब लिया है (डिलीवरी तारीख)",
        "total_price" to "कितने का लिया है (कुल कीमत ₹)",
        "fin_amount" to "कितने का फाइनेंस कराया है (₹)",
        "kist_details" to "कितनी किस्त है (EMI विवरण)",
        "veh_source" to "कहाँ से लिया है (एजेंसी / शोरूम)",
        "down_payment" to "डाउन पेमेंट (बचा हुआ ₹)",
        "battery_details" to "बैटरी विवरण एवं गारंटी (Battery Guarantee)",
        "motor_chassis" to "चेचिस एवं मोटर नंबर (Chassis & Motor No)",
        "charger_details" to "ईवी चार्जर विवरण (EV Charger)",
        "cust_photo" to "ग्राहक का फोटो (Customer Photo)",
        "warranty_photo" to "ई-रिक्शा गारंटी कार्ड फोटो",
        "battery_card_photo" to "बैटरी गारंटी कार्ड का फोटो",
        
        // Actions
        "btn_generate_bill" to "1-क्लिक में ई-रिक्शा बिल बनाएं और सेव करें",
        "btn_save_changes" to "बदलाव सेव करें",
        "btn_search_placeholder" to "नाम, फ़ोन, आधार, चेचिस या ई-रिक्शा मॉडल से खोजें...",
        "btn_share_whatsapp" to "व्हाट्सएप पर बिल भेजें",
        "btn_print_pdf" to "प्रिंट / PDF बिल निकालें",
        "btn_call" to "ग्राहक को कॉल करें",
        "btn_edit" to "एडिट करें",
        "btn_delete" to "डिलीट करें",
        "btn_export_2nd_phone" to "दूसरे मोबाइल के लिए डेटा भेजें (Export)",
        "btn_import_2nd_phone" to "दूसरे मोबाइल से डेटा लोड करें (Import)",
        "btn_take_photo" to "कैमरा से फोटो खींचें",
        "btn_choose_gallery" to "गैलरी से फोटो चुनें",
        "btn_remove_photo" to "फोटो हटाएं",
        
        // Headers & Labels
        "bill_header" to "इलेक्ट्रिक ई-रिक्शा डिलीवरी एवं फाइनेंस बिल पर्ची",
        "invoice_no" to "बिल संख्या (Invoice No)",
        "cust_sign" to "ग्राहक के हस्ताक्षर",
        "auth_sign" to "अधिकृत हस्ताक्षर (मेसर्स हुसैनी एंटरप्राइजेज)",
        "total_records" to "कुल ई-रिक्शा डिलीवरी",
        "total_sales" to "कुल बिक्री राशि",
        "total_financed" to "कुल फाइनेंस राशि",
        "no_records" to "कोई ई-रिक्शा रिकॉर्ड नहीं मिला",
        "sync_help_title" to "दोनों मोबाइल में डेटा कैसे देखें?",
        "sync_help_desc" to "मोहम्मद अनीस जी और मोहम्मद सुहैल जी दोनों के मोबाइल में पूरा ई-रिक्शा डेटा एक क्लिक में ट्रांसफर और सिंक हो जाता है।",
        "cloud_sync_url" to "गूगल शीट वेब ऐप लिंक (वैकल्पिक)",
        "cloud_sync_btn" to "गूगल शीट से लाइव सिंक करें",
        "success_saved" to "ई-रिक्शा बिल सफलतापूर्वक बन गया और सेव हो गया!",
        "success_imported" to "दूसरे फ़ोन से रिकॉर्ड सफलतापूर्वक लोड हो गए!",
        "confirm_delete" to "क्या आप वाकई इस ई-रिक्शा ग्राहक का रिकॉर्ड हटाना चाहते हैं?",
        "cancel" to "रद्द करें",
        "delete" to "हटाएं",
        "close" to "बंद करें"
    )
}
