package team.sopo.push.presentation.request

import javax.validation.constraints.NotNull

data class PushParcelsRequest(
    @field: NotNull(message = "* 업데이트할 택배 목록을 확인해주세요.")
    val parcelIds: MutableList<Long>? = null,
    @field: NotNull(message = "* 유저id를 입력해주세요.")
    val userId: Long? = null
)