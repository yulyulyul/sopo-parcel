package team.sopo.push


enum class NotificationType(val code: String) {
    PUSH_UPDATE_PARCEL("10001"),
    PUSH_FRIEND_RECOMMEND("20001"),
    PUSH_IMPORTANT_NOTICE("90001");
}