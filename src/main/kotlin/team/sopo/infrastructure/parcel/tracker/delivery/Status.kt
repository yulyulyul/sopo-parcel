package team.sopo.infrastructure.parcel.tracker.delivery

import com.fasterxml.jackson.annotation.JsonProperty

data class Status(
    @JsonProperty("id") val id: String,
    @JsonProperty("text") val text: String
)