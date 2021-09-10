package team.sopo.push

import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.update.UpdatedParcelInfo

abstract class PushService(protected val pushData: MutableList<UpdatedParcelInfo>){
    abstract fun sendPushMsg(userId: String)
    abstract fun addToPushList(parcel: Parcel)
}