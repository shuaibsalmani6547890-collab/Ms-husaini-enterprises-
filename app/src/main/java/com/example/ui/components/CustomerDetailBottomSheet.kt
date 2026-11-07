package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.CustomerRecord
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalNavyPrimary
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailBottomSheet(
    record: CustomerRecord,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onViewBill: (CustomerRecord) -> Unit,
    onDelete: (CustomerRecord) -> Unit,
    onEdit: (CustomerRecord) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var fullScreenPhotoUrl by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("customer_detail_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 18.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldGreen
                        ) {
                            Text(
                                text = "🛺 EV",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = record.custName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Invoice: ${record.invoiceNo} • ${record.purchaseDate}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("dismiss_sheet_btn")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Call & WhatsApp Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${record.custPhone}"))
                        context.startActivity(dialIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("call_customer_btn")
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = AppStrings.get(lang, "btn_call"), fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        PrintHelper.shareViaWhatsApp(context, record, lang)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("whatsapp_customer_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "WhatsApp", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "WhatsApp", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Photos Section (Customer Photo, E-Rickshaw Guarantee, Battery Card)
            Text(
                text = "Attached Photos & Guarantee Cards (फ़ोटो)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Customer Photo
                PhotoThumbnailCard(
                    title = "Customer",
                    path = record.custPhotoPath,
                    defaultIcon = Icons.Default.Person,
                    onClick = { record.custPhotoPath?.let { fullScreenPhotoUrl = it } },
                    modifier = Modifier.weight(1f)
                )

                // Vehicle Guarantee Card Photo
                PhotoThumbnailCard(
                    title = "Vehicle Card",
                    path = record.warPhotoPath,
                    defaultIcon = Icons.Default.Description,
                    onClick = { record.warPhotoPath?.let { fullScreenPhotoUrl = it } },
                    modifier = Modifier.weight(1f)
                )

                // Battery Guarantee Card Photo
                PhotoThumbnailCard(
                    title = "Battery Card",
                    path = record.batteryCardPhotoPath,
                    defaultIcon = Icons.Default.BatteryChargingFull,
                    onClick = { record.batteryCardPhotoPath?.let { fullScreenPhotoUrl = it } },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Full Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(
                        text = "🛺 E-Rickshaw & Battery Specifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldGreen
                    )
                    DetailItem(AppStrings.get(lang, "veh_model"), record.vehModel)
                    DetailItem(AppStrings.get(lang, "veh_no"), record.vehNo)
                    if (record.batteryDetails.isNotBlank()) {
                        DetailItem(AppStrings.get(lang, "battery_details"), record.batteryDetails)
                    }
                    if (record.motorChassisNo.isNotBlank()) {
                        DetailItem(AppStrings.get(lang, "motor_chassis"), record.motorChassisNo)
                    }
                    if (record.chargerDetails.isNotBlank()) {
                        DetailItem(AppStrings.get(lang, "charger_details"), record.chargerDetails)
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Finance & Payment Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = RoyalNavyPrimary
                    )
                    DetailItem(AppStrings.get(lang, "cust_phone"), record.custPhone)
                    DetailItem(AppStrings.get(lang, "cust_aadhaar"), record.custAadhaar)
                    DetailItem(AppStrings.get(lang, "purchase_date"), record.purchaseDate)
                    DetailItem(AppStrings.get(lang, "total_price"), PrintHelper.formatCurrency(record.totalPrice))
                    DetailItem(AppStrings.get(lang, "fin_amount"), PrintHelper.formatCurrency(record.finAmount))
                    DetailItem(AppStrings.get(lang, "down_payment"), PrintHelper.formatCurrency(record.downPayment))
                    DetailItem(AppStrings.get(lang, "kist_details"), record.kistDetails)
                    DetailItem(AppStrings.get(lang, "veh_source"), record.vehSource)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Button(
                onClick = { onViewBill(record) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_bill_slip_btn")
            ) {
                Icon(imageVector = Icons.Default.Receipt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "View / Print E-Rickshaw Bill (पर्ची)", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onEdit(record) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = AppStrings.get(lang, "btn_edit"))
                }

                Button(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = AppStrings.get(lang, "btn_delete"))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(text = AppStrings.get(lang, "confirm_delete")) },
            text = { Text(text = "${record.custName} (${record.vehModel} - ${record.vehNo})") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(record)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(AppStrings.get(lang, "delete"))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text(AppStrings.get(lang, "cancel"))
                }
            }
        )
    }

    // Full Screen Photo Zoom Modal
    fullScreenPhotoUrl?.let { path ->
        Dialog(onDismissRequest = { fullScreenPhotoUrl = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = File(path),
                    contentDescription = "Zoomed Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { fullScreenPhotoUrl = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun PhotoThumbnailCard(
    title: String,
    path: String?,
    defaultIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (path != null) {
                AsyncImage(
                    model = File(path),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = defaultIcon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
                    Text("No Photo", fontSize = 10.sp, color = Color.Gray)
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.65f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(3.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.45f))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.55f))
    }
}
