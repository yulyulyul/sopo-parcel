package team.sopo.domain.push

interface PushService {
    fun pushCompleteParcels(userId: Long, parcelIds: List<Long>)
    fun pushAwakenDevice(topic: String)
}