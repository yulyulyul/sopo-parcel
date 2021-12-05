package team.sopo.push.domain

class PushInfo {
    data class ParcelUpdateCompleteMessage(val pushToken: String, val parcelIds: List<Long>)
}