package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY createdAt DESC")
    fun getAllCustomers(): Flow<List<CustomerRecord>>

    @Query("""
        SELECT * FROM customers 
        WHERE custName LIKE '%' || :query || '%' 
           OR custPhone LIKE '%' || :query || '%' 
           OR custAadhaar LIKE '%' || :query || '%' 
           OR vehNo LIKE '%' || :query || '%' 
           OR vehModel LIKE '%' || :query || '%' 
           OR invoiceNo LIKE '%' || :query || '%' 
        ORDER BY createdAt DESC
    """)
    fun searchCustomers(query: String): Flow<List<CustomerRecord>>

    @Query("SELECT * FROM customers WHERE invoiceNo = :invoiceNo LIMIT 1")
    suspend fun getCustomerByInvoice(invoiceNo: String): CustomerRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerRecord>)

    @Update
    suspend fun updateCustomer(customer: CustomerRecord)

    @Delete
    suspend fun deleteCustomer(customer: CustomerRecord)

    @Query("DELETE FROM customers WHERE invoiceNo = :invoiceNo")
    suspend fun deleteByInvoice(invoiceNo: String)

    @Query("SELECT COUNT(*) FROM customers")
    fun getCustomerCount(): Flow<Int>
}
