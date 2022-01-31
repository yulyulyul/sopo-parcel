package team.sopo.push.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class PushToken(@JsonProperty("pushToken") val pushToken: String)
