package team.sopo.infrastructure.parcel.tracker

import com.fasterxml.jackson.annotation.JsonProperty

data class Carrier(
    @JsonProperty("id") val id: String?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("tel") val tel: String?
)