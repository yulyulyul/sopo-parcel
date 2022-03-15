package team.sopo.infrastructure.parcel.tracker.delivery

import com.fasterxml.jackson.annotation.JsonProperty

data class Progresses(
    @JsonProperty("time") val time: String?,
    @JsonProperty("location") val location: Location?,
    @JsonProperty("status") val status: Status,
    @JsonProperty("description") val description: String?
)