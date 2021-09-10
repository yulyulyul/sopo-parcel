package team.sopo.parcel.domain.command

data class RefreshParcelCommand(
    val userId: String,
    val parcelId: Long
)