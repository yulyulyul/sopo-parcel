package team.sopo.push.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class PushTokenDto(@JsonProperty("fcmToken") val pushToken: String?)
