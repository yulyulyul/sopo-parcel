package team.sopo.domain.parcel

import org.springframework.data.domain.Pageable

interface ParcelReader {
    fun getParcel(parcelId: Long, userId: Long): Parcel
    fun getParcel(userId: Long, carrier: Carrier, waybillNum: String): Parcel
    fun getParcels(parcelIds: List<Long>, userId: Long): List<Parcel>
    fun getOngoingParcels(userId: Long): List<Parcel>
    fun getCompleteParcels(userId: Long, inquiryDate: String, pageable: Pageable): List<Parcel>
    fun getRegisteredCountIn2Week(userId: Long): Long
    fun getRegisteredParcelCount(userId: Long): Long
    fun getMonthlyParcelCntList(userId: Long): List<ParcelInfo.MonthlyParcelCnt>
    fun getCurrentMonthRegisteredCount(userId: Long): Int
}