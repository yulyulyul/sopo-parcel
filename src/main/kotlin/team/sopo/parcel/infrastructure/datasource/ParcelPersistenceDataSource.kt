package team.sopo.parcel.infrastructure.datasource

import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.vo.ParcelCntInfo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ParcelPersistenceDataSource {
    fun getRegisterParcelCount(userId: String): Long
    fun getRegisterParcelCountIn2Week(userId: String): Long
    fun getParcelHistoryWithInOneWeek(userId: String): List<Parcel>?
    fun getCompleteParcels(pageable: Pageable, userId: String, startDate: String, endDate: String): Page<Parcel>
    fun getParcel(userId: String, parcelId: Long): Parcel
    fun getIncompleteMonth(userId: String): List<ParcelCntInfo>
    fun getOngoingParcels(userId: String): List<Parcel>?
    fun saveParcel(parcel: Parcel): Parcel
    fun deleteParcel(parcel: Parcel)
    fun isAlreadyRegistered(userId: String, waybillNum: String, carrier: String): Boolean
    fun isLimitCountOver(userId: String): Boolean
}