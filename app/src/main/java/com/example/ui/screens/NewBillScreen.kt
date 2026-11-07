package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.CustomerRecord
import com.example.data.ImageStorageHelper
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.MainViewModel
import com.example.ui.components.PrintHelper
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalNavyPrimary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBillScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang by viewModel.language.collectAsState()
    val editingRecord by viewModel.editingRecord.collectAsState()

    var custName by remember { mutableStateOf("") }
    var custPhone by remember { mutableStateOf("") }
    var custAadhaar by remember { mutableStateOf("") }
    var vehModel by remember { mutableStateOf("") }
    var vehNo by remember { mutableStateOf("") }
    var purchaseDate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var totalPriceStr by remember { mutableStateOf("") }
    var finAmountStr by remember { mutableStateOf("") }
    var kistDetails by remember { mutableStateOf("") }
    var vehSource by remember { mutableStateOf("Ms Husaini Enterprises E-Rickshaw Agency") }
    var batteryDetails by remember { mutableStateOf("") }
    var motorChassisNo by remember { mutableStateOf("") }
    var chargerDetails by remember { mutableStateOf("15A Smart EV Auto-Cut Charger") }

    var custPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var warPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var batteryCardPhotoUri by remember { mutableStateOf<Uri?>(null) }

    var existingCustPath by remember { mutableStateOf<String?>(null) }
    var existingWarPath by remember { mutableStateOf<String?>(null) }
    var existingBatteryCardPath by remember { mutableStateOf<String?>(null) }
    var activeEditingInvoiceNo by remember { mutableStateOf<String?>(null) }

    // Prepopulate if editing
    LaunchedEffect(editingRecord) {
        editingRecord?.let { rec ->
            activeEditingInvoiceNo = rec.invoiceNo
            custName = rec.custName
            custPhone = rec.custPhone
            custAadhaar = rec.custAadhaar
            vehModel = rec.vehModel
            vehNo = rec.vehNo
            purchaseDate = rec.purchaseDate
            totalPriceStr = if (rec.totalPrice > 0) rec.totalPrice.toInt().toString() else ""
            finAmountStr = if (rec.finAmount > 0) rec.finAmount.toInt().toString() else ""
            kistDetails = rec.kistDetails
            vehSource = rec.vehSource
            batteryDetails = rec.batteryDetails
            motorChassisNo = rec.motorChassisNo
            chargerDetails = rec.chargerDetails
            existingCustPath = rec.custPhotoPath
            existingWarPath = rec.warPhotoPath
            existingBatteryCardPath = rec.batteryCardPhotoPath
        }
    }

    // Photo pickers
    val custPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            custPhotoUri = uri
            existingCustPath = null
        }
    }

    val warPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            warPhotoUri = uri
            existingWarPath = null
        }
    }

    val batteryCardPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            batteryCardPhotoUri = uri
            existingBatteryCardPath = null
        }
    }

    // Camera capture
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoTarget by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            when (currentPhotoTarget) {
                "cust" -> {
                    custPhotoUri = tempCameraUri
                    existingCustPath = null
                }
                "war" -> {
                    warPhotoUri = tempCameraUri
                    existingWarPath = null
                }
                "bat" -> {
                    batteryCardPhotoUri = tempCameraUri
                    existingBatteryCardPath = null
                }
            }
        }
    }

    fun launchCamera(target: String) {
        currentPhotoTarget = target
        val (_, uri) = ImageStorageHelper.createTempCameraFile(context, target)
        tempCameraUri = uri
        cameraLauncher.launch(uri)
    }

    val popularERickshaws = listOf(
        "Mayuri Deluxe (सवारी)",
        "YC Yatri Super (ई-रिक्शा)",
        "Saarthi Star (पैसेंजर)",
        "Mahindra E-Alfa Mini",
        "Loader E-Rickshaw (मालवाहक)",
        "Piaggio Ape E-City",
        "City Life EV",
        "Husaini Special Heavy EV"
    )

    val popularBatteries = listOf(
        "Eastman 4x130Ah (12 Mo Guarantee)",
        "Lithium-ion 60V (3 Year Guarantee)",
        "Exide Neo 4-Battery (12 Mo)",
        "Livguard Heavy Duty (15 Mo)",
        "Okaya EV Power (12 Mo Guarantee)"
    )

    val totalPrice = totalPriceStr.toDoubleOrNull() ?: 0.0
    val finAmount = finAmountStr.toDoubleOrNull() ?: 0.0
    val downPayment = (totalPrice - finAmount).coerceAtLeast(0.0)

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(135.dp),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.husaini_hero_banner),
                    contentDescription = "E-Rickshaw Dealership Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.58f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldGreen
                        ) {
                            Text(
                                text = "100% ELECTRIC EV",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "MS HUSAINI ENTERPRISES",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Mohammad Anees • Mohammad Suhail | 📞 7668732106, 7906599049",
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }

        if (activeEditingInvoiceNo != null) {
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Editing Invoice: $activeEditingInvoiceNo",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    TextButton(onClick = {
                        viewModel.setEditingRecord(null)
                        activeEditingInvoiceNo = null
                        custName = ""
                        custPhone = ""
                        custAadhaar = ""
                        vehModel = ""
                        vehNo = ""
                        totalPriceStr = ""
                        finAmountStr = ""
                        kistDetails = ""
                        batteryDetails = ""
                        motorChassisNo = ""
                    }) {
                        Text("Cancel")
                    }
                }
            }
        }

        // Section 1: Customer Details
        Text(
            text = "1. Customer Information (ग्राहक की जानकारी)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = EmeraldGreen
        )

        OutlinedTextField(
            value = custName,
            onValueChange = { custName = it },
            label = { Text(AppStrings.get(lang, "cust_name")) },
            placeholder = { Text("Ex: Ramesh Kumar / अली अहमद") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cust_name_input")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = custPhone,
                onValueChange = { custPhone = it },
                label = { Text(AppStrings.get(lang, "cust_phone")) },
                placeholder = { Text("10 digit number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("cust_phone_input")
            )

            OutlinedTextField(
                value = custAadhaar,
                onValueChange = { custAadhaar = it },
                label = { Text(AppStrings.get(lang, "cust_aadhaar")) },
                placeholder = { Text("12 digit Aadhaar") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("cust_aadhaar_input")
            )
        }

        // Section 2: E-Rickshaw & EV Details
        Text(
            text = "2. E-Rickshaw & EV Details (ई-रिक्शा विवरण)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = EmeraldGreen,
            modifier = Modifier.padding(top = 4.dp)
        )

        OutlinedTextField(
            value = vehModel,
            onValueChange = { vehModel = it },
            label = { Text(AppStrings.get(lang, "veh_model")) },
            placeholder = { Text("Ex: Mayuri Deluxe Passenger E-Rickshaw") },
            leadingIcon = { Icon(Icons.Default.ElectricRickshaw, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("veh_model_input")
        )

        // E-Rickshaw quick chips
        Text(
            text = "Popular E-Rickshaw Models:",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(popularERickshaws) { model ->
                SuggestionChip(
                    onClick = { vehModel = model },
                    label = { Text(model, fontSize = 11.sp) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = vehNo,
                onValueChange = { vehNo = it.uppercase() },
                label = { Text(AppStrings.get(lang, "veh_no")) },
                placeholder = { Text("UP 25 ER 1234 / Temp No") },
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("veh_no_input")
            )

            OutlinedTextField(
                value = purchaseDate,
                onValueChange = { purchaseDate = it },
                label = { Text(AppStrings.get(lang, "purchase_date")) },
                placeholder = { Text("YYYY-MM-DD") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("purchase_date_input")
            )
        }

        OutlinedTextField(
            value = motorChassisNo,
            onValueChange = { motorChassisNo = it },
            label = { Text(AppStrings.get(lang, "motor_chassis")) },
            placeholder = { Text("Ex: Chassis: ME... | 1200W BLDC Motor") },
            leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Section 3: Battery & Charger Details (Crucial for EV!)
        Text(
            text = "3. Battery & Guarantee (बैटरी प्रकार एवं गारंटी)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = EmeraldGreen,
            modifier = Modifier.padding(top = 4.dp)
        )

        OutlinedTextField(
            value = batteryDetails,
            onValueChange = { batteryDetails = it },
            label = { Text(AppStrings.get(lang, "battery_details")) },
            placeholder = { Text("Ex: Eastman 4x130Ah Heavy Duty (12 Mo Guarantee)") },
            leadingIcon = { Icon(Icons.Default.BatteryChargingFull, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Battery quick chips
        Text(
            text = "Popular Battery & Guarantee Presets:",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(popularBatteries) { battery ->
                SuggestionChip(
                    onClick = { batteryDetails = battery },
                    label = { Text(battery, fontSize = 11.sp) }
                )
            }
        }

        OutlinedTextField(
            value = chargerDetails,
            onValueChange = { chargerDetails = it },
            label = { Text(AppStrings.get(lang, "charger_details")) },
            placeholder = { Text("15A Smart Auto-Cut Fast Charger") },
            leadingIcon = { Icon(Icons.Default.Power, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Section 4: Pricing & Finance
        Text(
            text = "4. Pricing & Finance (कीमत एवं किस्त विवरण)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = EmeraldGreen,
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = totalPriceStr,
                onValueChange = { totalPriceStr = it },
                label = { Text(AppStrings.get(lang, "total_price")) },
                placeholder = { Text("₹ 1,75,000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("total_price_input")
            )

            OutlinedTextField(
                value = finAmountStr,
                onValueChange = { finAmountStr = it },
                label = { Text(AppStrings.get(lang, "fin_amount")) },
                placeholder = { Text("₹ 1,20,000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("fin_amount_input")
            )
        }

        // Down Payment summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💵 " + AppStrings.get(lang, "down_payment"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = PrintHelper.formatCurrency(downPayment),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreen
                )
            }
        }

        OutlinedTextField(
            value = kistDetails,
            onValueChange = { kistDetails = it },
            label = { Text(AppStrings.get(lang, "kist_details")) },
            placeholder = { Text("Ex: 24 Kist x ₹5,600/mahina") },
            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("kist_details_input")
        )

        OutlinedTextField(
            value = vehSource,
            onValueChange = { vehSource = it },
            label = { Text(AppStrings.get(lang, "veh_source")) },
            placeholder = { Text("Ms Husaini Enterprises E-Rickshaw Agency") },
            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("veh_source_input")
        )

        // Section 5: Photo Attachments (Customer, Vehicle Card, Battery Card)
        Text(
            text = "5. Photos & Guarantee Cards (फ़ोटो एवं गारंटी कार्ड)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = EmeraldGreen,
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Customer Photo Picker Box
            PhotoUploadBox(
                title = "Customer",
                icon = Icons.Default.Person,
                uri = custPhotoUri,
                existingPath = existingCustPath,
                onGalleryClick = {
                    custPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onCameraClick = { launchCamera("cust") },
                onRemove = {
                    custPhotoUri = null
                    existingCustPath = null
                },
                modifier = Modifier.weight(1f)
            )

            // Vehicle Guarantee Card Photo
            PhotoUploadBox(
                title = "Vehicle Card",
                icon = Icons.Default.Description,
                uri = warPhotoUri,
                existingPath = existingWarPath,
                onGalleryClick = {
                    warPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onCameraClick = { launchCamera("war") },
                onRemove = {
                    warPhotoUri = null
                    existingWarPath = null
                },
                modifier = Modifier.weight(1f)
            )

            // Battery Guarantee Card Photo
            PhotoUploadBox(
                title = "Battery Card",
                icon = Icons.Default.BatteryChargingFull,
                uri = batteryCardPhotoUri,
                existingPath = existingBatteryCardPath,
                onGalleryClick = {
                    batteryCardPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onCameraClick = { launchCamera("bat") },
                onRemove = {
                    batteryCardPhotoUri = null
                    existingBatteryCardPath = null
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 1-Click Generate Bill Button
        Button(
            onClick = {
                if (custName.isBlank()) return@Button
                viewModel.saveCustomerAndGenerateBill(
                    invoiceNo = activeEditingInvoiceNo,
                    custName = custName,
                    custPhone = custPhone,
                    custAadhaar = custAadhaar,
                    vehModel = vehModel.ifBlank { "E-Rickshaw EV" },
                    vehNo = vehNo.ifBlank { "Temporary" },
                    purchaseDate = purchaseDate,
                    totalPrice = totalPrice,
                    finAmount = finAmount,
                    kistDetails = kistDetails.ifBlank { "Cash / Paid" },
                    vehSource = vehSource,
                    batteryDetails = batteryDetails,
                    motorChassisNo = motorChassisNo,
                    chargerDetails = chargerDetails,
                    custPhotoUri = custPhotoUri,
                    warPhotoUri = warPhotoUri,
                    batteryCardPhotoUri = batteryCardPhotoUri,
                    existingCustPhotoPath = existingCustPath,
                    existingWarPhotoPath = existingWarPath,
                    existingBatteryCardPhotoPath = existingBatteryCardPath
                )
                if (activeEditingInvoiceNo == null) {
                    custName = ""
                    custPhone = ""
                    custAadhaar = ""
                    vehModel = ""
                    vehNo = ""
                    totalPriceStr = ""
                    finAmountStr = ""
                    kistDetails = ""
                    batteryDetails = ""
                    motorChassisNo = ""
                    custPhotoUri = null
                    warPhotoUri = null
                    batteryCardPhotoUri = null
                }
            },
            enabled = custName.isNotBlank() && custPhone.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_bill_btn")
        ) {
            Icon(imageVector = Icons.Default.ElectricRickshaw, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = AppStrings.get(lang, "btn_generate_bill"),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun PhotoUploadBox(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    uri: Uri?,
    existingPath: String?,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(165.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderStroke),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldGreen,
                maxLines = 1
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uri != null -> {
                        AsyncImage(
                            model = uri,
                            contentDescription = title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    existingPath != null -> {
                        AsyncImage(
                            model = File(existingPath),
                            contentDescription = title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(26.dp))
                            Text("No Photo", fontSize = 9.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = onCameraClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Camera", tint = ElectricBlue, modifier = Modifier.size(16.dp))
                }
                IconButton(
                    onClick = onGalleryClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = RoyalNavyPrimary, modifier = Modifier.size(16.dp))
                }
                if (uri != null || existingPath != null) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
