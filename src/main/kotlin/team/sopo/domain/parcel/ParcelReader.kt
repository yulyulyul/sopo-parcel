package team.sopo.domain.parcel

import org.springframework.data.domain.Pageable
import team.sopo.domain.parcel.carrier.Carrier

interface ParcelReader {
    fun getParcel(parcelId: Long, userToken: String): Parcel
    fun getParcel(userToken: String, carrier: Carrier, waybillNum: String): Parcel
    fun getParcels(parcelIds: List<Long>, userToken: String): List<Parcel>
    fun getOngoingParcels(userToken: String): List<Parcel>
    fun getCompleteParcels(userToken: String, inquiryDate: String, pageable: Pageable, itemCnt: Int): List<Parcel>
    fun getRegisteredCountIn2Week(userToken: String): Long
    fun getRegisteredParcelCount(userToken: String): Long
    fun getMonthlyParcelCntList(userToken: String): List<ParcelInfo.MonthlyParcelCnt>
    fun getCurrentMonthRegisteredCount(userToken: String): Int
}