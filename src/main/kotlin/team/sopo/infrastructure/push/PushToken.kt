package team.sopo.infrastructure.push

import com.fasterxml.jackson.annotation.JsonProperty

data class PushToken(@JsonProperty("pushToken") val pushToken: String)
