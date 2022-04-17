package team.sopo.infrastructure.parcel.support

import team.sopo.domain.parcel.ParcelInfo
import team.sopo.domain.parcel.Parcel

interface ParcelRepositorySupport {

    fun getRegisterParcelCount(userToken: String): Long

    fun getRegisterParcelCountIn2Week(userToken: String): Long

    fun getParcel(userToken: String, parcelId: Long): Parcel

    fun getParcelsOngoing(userToken: String): List<Parcel>?

    fun isAlreadyRegistered(userToken: String, waybillNum: String, carrier: String): Boolean

    fun getMonthlyParcelCntList(userToken: String): MutableList<ParcelInfo.MonthlyParcelCnt>

    fun isLimitCountOver(userToken: String): Boolean

    fun getCurrentMonthRegisteredCount(userToken: String): Int
}