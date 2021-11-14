package team.sopo.parcel.domain

import org.springframework.data.domain.Pageable
import team.sopo.parcel.ParcelInfo

interface ParcelReader {
    fun getParcel(parcelId: Long, userId: String): Parcel
    fun getParcel(userId: String, carrier: Carrier, waybillNum: String): Parcel
    fun getOngoingParcels(userId: String): List<Parcel>
    fun getCompleteParcels(userId: String, inquiryDate: String, pageable: Pageable): List<Parcel>
    fun getRegisteredCountIn2Week(userId: String): Long
    fun getRegisteredParcelCount(userId: String): Long
    fun getMonthlyParcelCntList(userId: String): List<ParcelInfo.MonthlyParcelCnt>
    fun getCurrentMonthRegisteredCount(userId: String): Int
}