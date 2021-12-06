package team.sopo.push.domain

interface PushService {
    fun pushCompleteParcels(userId: String, parcelIds: List<Long>)
    fun pushAwakenDevice(topic: String)
}