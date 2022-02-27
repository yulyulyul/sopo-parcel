package team.sopo.infrastructure.parcel.support

import team.sopo.domain.parcel.ParcelInfo
import team.sopo.domain.parcel.Parcel

interface ParcelRepositorySupport {

    fun getRegisterParcelCount(userId: Long): Long

    fun getRegisterParcelCountIn2Week(userId: Long): Long

    fun getParcel(userId: Long, parcelId: Long): Parcel

    fun getParcelsOngoing(userId: Long): List<Parcel>?

    fun isAlreadyRegistered(userId: Long, waybillNum: String, carrier: String): Boolean

    fun getMonthlyParcelCntList(userId: Long): MutableList<ParcelInfo.MonthlyParcelCnt>

    fun isLimitCountOver(userId: Long): Boolean

    fun getCurrentMonthRegisteredCount(userId: Long): Int
}