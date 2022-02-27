package team.sopo.domain.push

class PushInfo {
    data class ParcelUpdateCompleteMessage(val pushToken: String, val parcelIds: List<Long>)
    data class DeviceAwakenMessage(val topic: String)
}