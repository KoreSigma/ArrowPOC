package models

import kotlinx.serialization.Serializable

@Serializable
class DummyCsvEntryMillion(
    val age: Int,
    val dollar: String,
    val longitude: Double,
    val latitude: Double,
    val zip: Int,
    val integer: Int,
    val ccnumber: Long
)