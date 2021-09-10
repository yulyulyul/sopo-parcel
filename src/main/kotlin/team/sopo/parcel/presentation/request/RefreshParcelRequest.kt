package team.sopo.parcel.presentation.request

import javax.validation.constraints.NotNull

data class RefreshParcelRequest(
    @NotNull(message = "* 택배 id를 확인해주세요.")
    val parcelId: Long? = null
)