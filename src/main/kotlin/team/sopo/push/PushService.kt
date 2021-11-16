package team.sopo.push

import team.sopo.parcel.ParcelInfo
import team.sopo.push.dto.UpdatedParcelInfo

abstract class PushService(protected val pushData: MutableList<UpdatedParcelInfo>) {
    abstract fun sendPushMsg(userId: String)
    abstract fun addToPushList(parcel: ParcelInfo.Main)
    abstract fun addToPushList(parcelList: List<ParcelInfo.Main>)
}