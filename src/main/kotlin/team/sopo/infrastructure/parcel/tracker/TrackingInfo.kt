package team.sopo.infrastructure.parcel.tracker

import com.fasterxml.jackson.annotation.JsonProperty

data class TrackingInfo(
    @JsonProperty("from") val from: From?,
    @JsonProperty("to") val to: To?,
    @JsonProperty("state") val state: State,
    @JsonProperty("item") val item: String?,
    @JsonProperty("progresses") var progresses: MutableList<Progresses?>,
    @JsonProperty("carrier") val carrier: Carrier?
)