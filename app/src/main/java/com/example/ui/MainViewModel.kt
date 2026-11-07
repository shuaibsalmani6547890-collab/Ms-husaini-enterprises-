package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CustomerRecord
import com.example.data.ImageStorageHelper
import com.example.data.SyncManager
import com.example.model.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val customerDao = db.customerDao()

    private val _language = MutableStateFlow(AppLanguage.HINDI)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val customers: StateFlow<List<CustomerRecord>> = _searchQuery
        .debounce(150)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                customerDao.getAllCustomers()
            } else {
                customerDao.searchCustomers(query.trim())
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCustomersList: StateFlow<List<CustomerRecord>> = customerDao.getAllCustomers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCustomer = MutableStateFlow<CustomerRecord?>(null)
    val selectedCustomer: StateFlow<CustomerRecord?> = _selectedCustomer.asStateFlow()

    private val _activeInvoiceRecord = MutableStateFlow<CustomerRecord?>(null)
    val activeInvoiceRecord: StateFlow<CustomerRecord?> = _activeInvoiceRecord.asStateFlow()

    private val _editingRecord = MutableStateFlow<CustomerRecord?>(null)
    val editingRecord: StateFlow<CustomerRecord?> = _editingRecord.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _cloudWebhookUrl = MutableStateFlow(
        application.getSharedPreferences("husaini_prefs", Application.MODE_PRIVATE)
            .getString("webhook_url", "") ?: ""
    )
    val cloudWebhookUrl: StateFlow<String> = _cloudWebhookUrl.asStateFlow()

    init {
        // Load initial E-Rickshaw EV sample if database is empty
        viewModelScope.launch(Dispatchers.IO) {
            customerDao.getCustomerCount().collect { count ->
                if (count == 0) {
                    val sample = CustomerRecord(
                        invoiceNo = "HE-2026-1001",
                        custName = "Ramesh Kumar",
                        custPhone = "9876543210",
                        custAadhaar = "4567 8901 2345",
                        vehModel = "Mayuri Deluxe Passenger E-Rickshaw",
                        vehNo = "UP 25 ER 1024",
                        purchaseDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        totalPrice = 175000.0,
                        finAmount = 120000.0,
                        kistDetails = "24 Kist x ₹5,600/mahina",
                        vehSource = "Ms Husaini Enterprises E-Rickshaw Showroom",
                        batteryDetails = "Eastman 4x130Ah Heavy Duty (12 Month Guarantee)",
                        motorChassisNo = "Chassis: ME26ER0981 | 1200W High Torque BLDC",
                        chargerDetails = "15A Smart Auto-Cut Fast Charger",
                        notes = "E-Rickshaw passenger delivery"
                    )
                    customerDao.insertCustomer(sample)
                }
            }
        }
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.HINDI) AppLanguage.ENGLISH else AppLanguage.HINDI
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCustomer(record: CustomerRecord?) {
        _selectedCustomer.value = record
    }

    fun openInvoiceSlip(record: CustomerRecord) {
        _activeInvoiceRecord.value = record
    }

    fun closeInvoiceSlip() {
        _activeInvoiceRecord.value = null
    }

    fun setEditingRecord(record: CustomerRecord?) {
        _editingRecord.value = record
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun setCloudWebhookUrl(url: String) {
        _cloudWebhookUrl.value = url
        getApplication<Application>().getSharedPreferences("husaini_prefs", Application.MODE_PRIVATE)
            .edit()
            .putString("webhook_url", url)
            .apply()
    }

    fun saveCustomerAndGenerateBill(
        invoiceNo: String?,
        custName: String,
        custPhone: String,
        custAadhaar: String,
        vehModel: String,
        vehNo: String,
        purchaseDate: String,
        totalPrice: Double,
        finAmount: Double,
        kistDetails: String,
        vehSource: String,
        batteryDetails: String,
        motorChassisNo: String,
        chargerDetails: String,
        custPhotoUri: Uri?,
        warPhotoUri: Uri?,
        batteryCardPhotoUri: Uri?,
        existingCustPhotoPath: String?,
        existingWarPhotoPath: String?,
        existingBatteryCardPhotoPath: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            
            val finalInvNo = invoiceNo ?: ("HE-" + SimpleDateFormat("yyyy-MMdd", Locale.getDefault()).format(Date()) + "-" + (1000..9999).random())

            val savedCustPhoto = if (custPhotoUri != null) {
                ImageStorageHelper.saveUriToFile(context, custPhotoUri, "cust_${finalInvNo}")
            } else existingCustPhotoPath

            val savedWarPhoto = if (warPhotoUri != null) {
                ImageStorageHelper.saveUriToFile(context, warPhotoUri, "war_${finalInvNo}")
            } else existingWarPhotoPath

            val savedBatteryCardPhoto = if (batteryCardPhotoUri != null) {
                ImageStorageHelper.saveUriToFile(context, batteryCardPhotoUri, "bat_${finalInvNo}")
            } else existingBatteryCardPhotoPath

            val record = CustomerRecord(
                invoiceNo = finalInvNo,
                custName = custName.trim(),
                custPhone = custPhone.trim(),
                custAadhaar = custAadhaar.trim(),
                vehModel = vehModel.trim(),
                vehNo = vehNo.trim(),
                purchaseDate = purchaseDate.ifBlank {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                },
                totalPrice = totalPrice,
                finAmount = finAmount,
                kistDetails = kistDetails.trim(),
                vehSource = vehSource.trim(),
                batteryDetails = batteryDetails.trim(),
                motorChassisNo = motorChassisNo.trim(),
                chargerDetails = chargerDetails.trim(),
                custPhotoPath = savedCustPhoto,
                warPhotoPath = savedWarPhoto,
                batteryCardPhotoPath = savedBatteryCardPhoto
            )

            customerDao.insertCustomer(record)
            _activeInvoiceRecord.value = record
            _editingRecord.value = null
            _statusMessage.value = "E-Rickshaw Bill Saved & Generated!"
        }
    }

    fun deleteCustomer(record: CustomerRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            customerDao.deleteCustomer(record)
            if (_selectedCustomer.value?.invoiceNo == record.invoiceNo) {
                _selectedCustomer.value = null
            }
            _statusMessage.value = "Customer Record Deleted."
        }
    }

    fun importRecordsFromUri(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val content = reader.readText()
                reader.close()

                val imported = SyncManager.importFromJson(content)
                if (imported.isNotEmpty()) {
                    customerDao.insertAll(imported)
                    _statusMessage.value = "Imported ${imported.size} records from 2nd phone!"
                } else {
                    _statusMessage.value = "No valid records found in file."
                }
            } catch (e: Exception) {
                _statusMessage.value = "Import error: ${e.localizedMessage}"
            }
        }
    }

    fun syncWithCloudWebhook() {
        val url = _cloudWebhookUrl.value.trim()
        if (url.isBlank()) {
            _statusMessage.value = "Please enter your Google Apps Script URL first."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            try {
                val records = allCustomersList.value
                val result = SyncManager.syncWithWebhook(url, records)
                result.fold(
                    onSuccess = { _statusMessage.value = "Cloud Sync Successful!" },
                    onFailure = { _statusMessage.value = "Sync failed: ${it.message}" }
                )
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
