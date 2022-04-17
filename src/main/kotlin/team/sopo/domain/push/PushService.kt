package team.sopo.domain.push

interface PushService {
    fun pushCompleteParcels(userToken: String, parcelIds: List<Long>)
    fun pushAwakenDevice(topic: String)
}