package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CustomerRecord
import com.example.data.SyncManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Husaini Enterprises", appName)
  }

  @Test
  fun `test customer record down payment calculation`() {
    val customer = CustomerRecord(
      invoiceNo = "HE-2026-0001",
      custName = "Mohammad Anees",
      custPhone = "7668732106",
      custAadhaar = "123456789012",
      vehModel = "Mayuri Deluxe E-Rickshaw",
      vehNo = "UP 25 ER 1234",
      purchaseDate = "2026-11-06",
      totalPrice = 175000.0,
      finAmount = 120000.0,
      kistDetails = "24 Kist x 5600",
      vehSource = "Ms Husaini Enterprises E-Rickshaw Agency",
      batteryDetails = "Eastman 4x130Ah (12 Mo Guarantee)",
      motorChassisNo = "Chassis: ME9988 | 1200W BLDC"
    )
    assertEquals(55000.0, customer.downPayment, 0.01)
  }

  @Test
  fun `test customer sync export and import`() {
    val record = CustomerRecord(
      invoiceNo = "HE-2026-0002",
      custName = "Mohammad Suhail",
      custPhone = "7906599049",
      custAadhaar = "987654321098",
      vehModel = "YC Yatri Super Loader E-Rickshaw",
      vehNo = "UP 25 ER 5678",
      purchaseDate = "2026-11-06",
      totalPrice = 185000.0,
      finAmount = 130000.0,
      kistDetails = "36 Kist x 4800",
      vehSource = "Husaini Showroom",
      batteryDetails = "Lithium-ion 60V 100Ah",
      motorChassisNo = "1500W High Torque"
    )

    val json = SyncManager.exportToJson(listOf(record))
    assertNotNull(json)

    val importedList = SyncManager.importFromJson(json)
    assertEquals(1, importedList.size)
    assertEquals("Mohammad Suhail", importedList[0].custName)
    assertEquals("HE-2026-0002", importedList[0].invoiceNo)
    assertEquals("Lithium-ion 60V 100Ah", importedList[0].batteryDetails)
  }
}
