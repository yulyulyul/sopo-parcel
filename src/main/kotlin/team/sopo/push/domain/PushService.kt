package team.sopo.push.domain

interface PushService {
    fun pushCompleteParcels(userId: Long, parcelIds: List<Long>)
    fun pushAwakenDevice(topic: String)
}