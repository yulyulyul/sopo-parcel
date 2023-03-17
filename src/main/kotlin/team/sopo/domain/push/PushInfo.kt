package team.sopo.domain.push

class PushInfo {
    data class UpdateParcel(val pushToken: String, val parcelIds: List<Long>)
    data class AwakeDevice(val topic: String)
}