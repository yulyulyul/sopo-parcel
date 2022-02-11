package team.sopo.push.presentation.request

import javax.validation.constraints.NotNull

data class DeviceAwakenRequest(
    @field: NotNull(message = "* 푸시를 보낼 토픽을 입력해주세요.")
    val topic: String? = null
)