package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.MainViewModel
import com.example.ui.components.BillSlipDialog
import com.example.ui.components.CustomerDetailBottomSheet
import com.example.ui.screens.AgencyScreen
import com.example.ui.screens.CustomerListScreen
import com.example.ui.screens.NewBillScreen
import com.example.ui.screens.SyncScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyPrimary

enum class AppNavTab(val labelKey: String, val icon: ImageVector) {
    NEW_BILL("nav_new_bill", Icons.Default.ReceiptLong),
    CUSTOMERS("nav_customers", Icons.Default.People),
    SYNC("nav_sync", Icons.Default.SyncAlt),
    AGENCY("nav_agency", Icons.Default.Business)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val lang by viewModel.language.collectAsState()
                val activeInvoice by viewModel.activeInvoiceRecord.collectAsState()
                val selectedCustomer by viewModel.selectedCustomer.collectAsState()
                val statusMessage by viewModel.statusMessage.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                var currentTab by remember { mutableStateOf(AppNavTab.NEW_BILL) }

                // Show status messages
                LaunchedEffect(statusMessage) {
                    statusMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearStatusMessage()
                    }
                }

                // Handle back button: if in another tab, return to NEW_BILL
                BackHandler(enabled = currentTab != AppNavTab.NEW_BILL) {
                    currentTab = AppNavTab.NEW_BILL
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = AppStrings.get(lang, "app_title"),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = RoyalNavyDark
                                    )
                                    Text(
                                        text = "Mohammad Anees • Mohammad Suhail",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            actions = {
                                // Language Switcher Button (English ⇋ Hindi)
                                FilledTonalButton(
                                    onClick = { viewModel.toggleLanguage() },
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .testTag("lang_toggle_btn"),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = "Language",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (lang == AppLanguage.HINDI) "English" else "हिंदी",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp
                        ) {
                            AppNavTab.values().forEach { tab ->
                                val selected = currentTab == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = AppStrings.get(lang, tab.labelKey)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = AppStrings.get(lang, tab.labelKey),
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = RoyalNavyPrimary,
                                        selectedTextColor = RoyalNavyPrimary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            AppNavTab.NEW_BILL -> NewBillScreen(viewModel = viewModel)
                            AppNavTab.CUSTOMERS -> CustomerListScreen(viewModel = viewModel)
                            AppNavTab.SYNC -> SyncScreen(viewModel = viewModel)
                            AppNavTab.AGENCY -> AgencyScreen(viewModel = viewModel)
                        }
                    }
                }

                // Active Invoice / Delivery Slip Dialog
                activeInvoice?.let { record ->
                    BillSlipDialog(
                        record = record,
                        lang = lang,
                        onDismiss = { viewModel.closeInvoiceSlip() }
                    )
                }

                // Customer Full Form & Details Bottom Sheet
                selectedCustomer?.let { record ->
                    CustomerDetailBottomSheet(
                        record = record,
                        lang = lang,
                        onDismiss = { viewModel.selectCustomer(null) },
                        onViewBill = { rec ->
                            viewModel.selectCustomer(null)
                            viewModel.openInvoiceSlip(rec)
                        },
                        onDelete = { rec ->
                            viewModel.deleteCustomer(rec)
                        },
                        onEdit = { rec ->
                            viewModel.selectCustomer(null)
                            viewModel.setEditingRecord(rec)
                            currentTab = AppNavTab.NEW_BILL
                        }
                    )
                }
            }
        }
    }
}
