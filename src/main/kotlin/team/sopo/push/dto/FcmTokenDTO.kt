package team.sopo.push.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class FcmTokenDTO(@JsonProperty("fcmToken") val fcmToken: String?)
