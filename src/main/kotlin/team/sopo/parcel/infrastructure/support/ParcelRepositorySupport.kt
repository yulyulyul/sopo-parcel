package team.sopo.parcel.infrastructure.support

import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.vo.ParcelCntInfo

interface ParcelRepositorySupport {

    fun getRegisterParcelCount(userId: String): Long

    fun getRegisterParcelCountIn2Week(userId: String): Long

    fun getParcel(userId: String, parcelId: Long): Parcel

    fun getParcelsOngoing(userId: String): List<Parcel>?

    fun isAlreadyRegistered(userId: String, waybillNum: String, carrier: String): Boolean

    fun getIncompleteMonthList(userId: String): MutableList<ParcelCntInfo>

    fun isLimitCountOver(userId: String): Boolean
}