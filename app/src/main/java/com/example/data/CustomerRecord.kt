package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "customers")
data class CustomerRecord(
    @PrimaryKey
    val invoiceNo: String,
    val custName: String,
    val custPhone: String,
    val custAadhaar: String,
    val vehModel: String,            // E-Rickshaw Model (Passenger / Loader / Mayuri / Saarthi / etc.)
    val vehNo: String,               // Vehicle No / Temporary No / Registration No
    val purchaseDate: String,
    val totalPrice: Double,
    val finAmount: Double,
    val kistDetails: String,
    val vehSource: String,
    val batteryDetails: String = "", // e.g. Eastman 4x130Ah (12 Mo) / Lithium 60V (3 Yr)
    val motorChassisNo: String = "", // Chassis / Motor Number
    val chargerDetails: String = "", // Smart EV Charger details
    val custPhotoPath: String? = null,
    val warPhotoPath: String? = null, // Vehicle guarantee/warranty card photo
    val batteryCardPhotoPath: String? = null, // Battery guarantee card photo
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    val downPayment: Double
        get() = (totalPrice - finAmount).coerceAtLeast(0.0)
}
