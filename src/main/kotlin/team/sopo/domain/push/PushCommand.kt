package team.sopo.domain.push

class PushCommand {
    data class PushToAwakeDevice(
        val topic: String
    )

    data class PushToParcelUpdate(
        val pushToken: String,
        val parcelIds: List<Long>
    )
}