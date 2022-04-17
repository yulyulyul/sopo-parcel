package team.sopo.interfaces.push

import javax.validation.constraints.NotNull

class PushDto {
    data class DeviceAwakenRequest(
        @field: NotNull(message = "* 푸시를 보낼 토픽을 입력해주세요.")
        val topic: String? = null
    )
    data class PushParcelsRequest(
        @field: NotNull(message = "* 업데이트할 택배 목록을 확인해주세요.")
        val parcelIds: MutableList<Long>? = null,
        @field: NotNull(message = "* userToken를 입력해주세요.")
        val userToken: String? = null
    )
}