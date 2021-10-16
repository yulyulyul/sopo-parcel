package team.sopo.parcel.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import team.sopo.parcel.domain.vo.ParcelCntInfo

interface ParcelRepository {
    fun getRegisterParcelCount(userId: String): Long
    fun getRegisterParcelCountIn2Week(userId: String): Long
    fun getParcelHistoryWithInOneWeek(userId: String): List<Parcel>?
    fun getCompleteParcels(pageable: Pageable, userId: String, startDate: String, endDate: String): Page<Parcel>

    fun getParcel(userId: String, parcelId: Long): Parcel
    fun getRefreshedParcel(userId: String, parcelId: Long): Parcel
    fun getParcelFromRemote(carrier: String, waybillNum: String, userId: String, alias: String): Parcel
    fun getOngoingParcels(userId: String): List<Parcel>?
    fun save(parcel: Parcel): Parcel

    fun delete(parcel: Parcel)

    fun isAlreadyRegistered(userId: String, waybillNum: String, carrier: String): Boolean
    fun getIncompleteMonth(userId: String): List<ParcelCntInfo>
    fun isLimitCountOver(userId: String): Boolean
}