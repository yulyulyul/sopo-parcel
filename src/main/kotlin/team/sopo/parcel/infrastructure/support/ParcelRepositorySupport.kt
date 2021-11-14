package team.sopo.parcel.infrastructure.support

import team.sopo.parcel.ParcelInfo
import team.sopo.parcel.domain.Parcel

interface ParcelRepositorySupport {

    fun getRegisterParcelCount(userId: String): Long

    fun getRegisterParcelCountIn2Week(userId: String): Long

    fun getParcel(userId: String, parcelId: Long): Parcel

    fun getParcelsOngoing(userId: String): List<Parcel>?

    fun isAlreadyRegistered(userId: String, waybillNum: String, carrier: String): Boolean

    fun getMonthlyParcelCntList(userId: String): MutableList<ParcelInfo.MonthlyParcelCnt>

    fun isLimitCountOver(userId: String): Boolean

    fun getCurrentMonthRegisteredCount(userId: String): Int
}