package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.AsyncImage
import com.example.data.CustomerRecord
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.MainViewModel
import com.example.ui.components.PrintHelper
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyPrimary
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang by viewModel.language.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val allCustomers by viewModel.allCustomersList.collectAsState()

    val totalSales = remember(allCustomers) { allCustomers.sumOf { it.totalPrice } }
    val totalFinanced = remember(allCustomers) { allCustomers.sumOf { it.finAmount } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Search Bar (Instant lookup by Name, Phone, Aadhaar, Vehicle No, Chassis)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = {
                Text(
                    text = AppStrings.get(lang, "btn_search_placeholder"),
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = EmeraldGreen
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("customer_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // EV Metrics Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricColumn(
                    title = AppStrings.get(lang, "total_records"),
                    value = allCustomers.size.toString() + " EV",
                    color = EmeraldGreen
                )
                Divider(
                    modifier = Modifier
                        .height(34.dp)
                        .width(1.dp)
                )
                MetricColumn(
                    title = AppStrings.get(lang, "total_sales"),
                    value = PrintHelper.formatCurrency(totalSales),
                    color = EmeraldGreen
                )
                Divider(
                    modifier = Modifier
                        .height(34.dp)
                        .width(1.dp)
                )
                MetricColumn(
                    title = AppStrings.get(lang, "total_financed"),
                    value = PrintHelper.formatCurrency(totalFinanced),
                    color = ElectricBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Customers List
        if (customers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ElectricRickshaw,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = AppStrings.get(lang, "no_records"),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (searchQuery.isNotEmpty()) {
                        TextButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Text("Clear filter")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(customers, key = { it.invoiceNo }) { customer ->
                    CustomerCardItem(
                        record = customer,
                        lang = lang,
                        onClick = { viewModel.selectCustomer(customer) },
                        onViewBill = { viewModel.openInvoiceSlip(customer) },
                        onShareWhatsApp = {
                            PrintHelper.shareViaWhatsApp(context, customer, lang)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun CustomerCardItem(
    record: CustomerRecord,
    lang: AppLanguage,
    onClick: () -> Unit,
    onViewBill: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("customer_card_${record.invoiceNo}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Customer Photo Thumbnail or Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFD1FAE5)),
                    contentAlignment = Alignment.Center
                ) {
                    if (record.custPhotoPath != null) {
                        AsyncImage(
                            model = File(record.custPhotoPath),
                            contentDescription = record.custName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = record.custName.take(1).uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Name & E-Rickshaw info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = record.custName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavyDark
                        )
                        Text(
                            text = record.purchaseDate,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "🛺 ${record.vehModel} • ${record.vehNo}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldGreen,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Phone, Aadhaar, Price & Installment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF0FDF4),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📞 ${record.custPhone} | 🆔 ${record.custAadhaar}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (record.batteryDetails.isNotBlank()) {
                        Text(
                            text = "🔋 ${record.batteryDetails}",
                            fontSize = 11.sp,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "Kist: ${record.kistDetails}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavyDark
                    )
                }

                Text(
                    text = PrintHelper.formatCurrency(record.totalPrice),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldGreen
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo badges
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (record.custPhotoPath != null) {
                        Badge(containerColor = EmeraldGreen) {
                            Text("📸 Photo", fontSize = 9.sp, color = Color.White)
                        }
                    }
                    if (record.warPhotoPath != null) {
                        Badge(containerColor = ElectricBlue) {
                            Text("📄 E-Rickshaw", fontSize = 9.sp, color = Color.White)
                        }
                    }
                    if (record.batteryCardPhotoPath != null) {
                        Badge(containerColor = Color(0xFFD97706)) {
                            Text("🔋 Battery", fontSize = 9.sp, color = Color.White)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onShareWhatsApp,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "WhatsApp",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onViewBill,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = "Bill",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    TextButton(
                        onClick = onClick,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text("Open Form >", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
