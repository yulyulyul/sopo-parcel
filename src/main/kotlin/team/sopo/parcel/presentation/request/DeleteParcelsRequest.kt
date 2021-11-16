package team.sopo.parcel.presentation.request

import javax.validation.constraints.NotNull

data class DeleteParcelsRequest(
    @field: NotNull(message = "* 삭제할 택배 목록을 확인해주세요.")
    val parcelIds: MutableList<Long>? = null
)