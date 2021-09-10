package team.sopo.parcel.domain.command

data class GetParcelCommand(
    val userId: String,
    val parcelId: Long
)