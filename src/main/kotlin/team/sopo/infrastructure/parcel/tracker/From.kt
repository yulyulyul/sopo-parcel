package team.sopo.infrastructure.parcel.tracker

import com.fasterxml.jackson.annotation.JsonProperty

data class From(
    @JsonProperty("name") var name: String?,
    @JsonProperty("time") var time: String?,
    @JsonProperty("tel") var tel: String?
)