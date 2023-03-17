package team.sopo.domain.push

interface PushService {
    fun pushToUpdateParcel(userToken: String, parcelIds: List<Long>)
    fun pushToAwakenDevice(topic: String)
}