package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SyncManager
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.MainViewModel
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalNavyPrimary

@Composable
fun SyncScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang by viewModel.language.collectAsState()
    val allCustomers by viewModel.allCustomersList.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val cloudWebhookUrl by viewModel.cloudWebhookUrl.collectAsState()

    var webhookInput by remember(cloudWebhookUrl) { mutableStateOf(cloudWebhookUrl) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importRecordsFromUri(uri)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanatory Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SyncAlt,
                        contentDescription = null,
                        tint = RoyalNavyPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AppStrings.get(lang, "sync_help_title"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavyPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = AppStrings.get(lang, "sync_help_desc"),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Method 1: Export Data to 2nd Phone
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "तरीका 1: दूसरे फ़ोन पर डेटा भेजें (Export to 2nd Phone)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalNavyPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "अपने इस मोबाइल से सभी ${allCustomers.size} ग्राहकों का डेटा व्हाट्सएप या ब्लूटूथ से दूसरे मोबाइल (अनीस जी या सुहैल जी) को भेजें।",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val fileUri = SyncManager.createExportFile(context, allCustomers)
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(Intent.EXTRA_STREAM, fileUri)
                            putExtra(Intent.EXTRA_SUBJECT, "Husaini Enterprises Data Backup")
                            putExtra(Intent.EXTRA_TEXT, "Husaini Enterprises Customer Records (${allCustomers.size} customers)")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Backup to 2nd Phone"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_data_btn")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get(lang, "btn_export_2nd_phone"),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Method 2: Import Data from 2nd Phone
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "तरीका 2: दूसरे फ़ोन से डेटा प्राप्त करें (Import on this Phone)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalNavyPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "व्हाट्सएप या फ़ाइल से प्राप्त हुआ बैकअप यहाँ से चुनें। सभी रिकॉर्ड्स बिना डुप्लिकेट हुए अपने आप जुड़ जाएंगे।",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        filePickerLauncher.launch("*/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("import_data_btn")
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get(lang, "btn_import_2nd_phone"),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Method 3: Google Sheet / Cloud Webhook Integration
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "तरीका 3: Google Sheets / Apps Script लाइव सिंक (Cloud)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalNavyPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "यदि आपने Google Sheets Web App लिंक बनाया है, तो उसे यहाँ डालें। दोनों मोबाइल सीधे एक ही गूगल शीट से सिंक रहेंगे।",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = webhookInput,
                    onValueChange = {
                        webhookInput = it
                        viewModel.setCloudWebhookUrl(it)
                    },
                    label = { Text("Google Apps Script Web App URL") },
                    placeholder = { Text("https://script.google.com/macros/s/.../exec") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.syncWithCloudWebhook()
                    },
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cloud_sync_btn")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Syncing with Cloud...")
                    } else {
                        Icon(imageVector = Icons.Default.CloudSync, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get(lang, "cloud_sync_btn"),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Step by step guide in cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "📋 दोनों मोबाइल में उपयोग करने के आसान 3 स्टेप्स:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = RoyalNavyPrimary
                )
                Text(
                    text = "1. जब भी अनीस जी नए ग्राहक का बिल बनाएंगे, तो डेटा इस फोन में सुरक्षित रहेगा।",
                    fontSize = 12.sp
                )
                Text(
                    text = "2. दिन के अंत में 'Export to 2nd Phone' बटन दबाकर व्हाट्सएप पर सुहैल जी को भेज दें।",
                    fontSize = 12.sp
                )
                Text(
                    text = "3. सुहैल जी अपने फोन में 'Import from 2nd Phone' बटन दबाकर उस फाइल को चुनेंगे, तो तुरंत सभी ग्राहकों की गाड़ियां, किस्त और फोटो उनके फोन में आ जाएंगी।",
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
