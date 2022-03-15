package team.sopo.infrastructure.parcel.tracker.delivery

import com.fasterxml.jackson.annotation.JsonProperty

data class DeliveryTrackingInfo(
    @JsonProperty("from") val from: From?,
    @JsonProperty("to") val to: To?,
    @JsonProperty("state") val state: State,
    @JsonProperty("item") val item: String?,
    @JsonProperty("progresses") var progresses: MutableList<Progresses?>,
    @JsonProperty("carrier") val carrier: Carrier?
)