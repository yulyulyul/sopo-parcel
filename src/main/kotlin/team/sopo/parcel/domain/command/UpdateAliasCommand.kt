package team.sopo.parcel.domain.command

data class UpdateAliasCommand(
    val userId: String,
    val parcelId: Long,
    val alias: String
)