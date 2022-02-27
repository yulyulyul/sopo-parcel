package team.sopo.domain.parcel.trackinginfo

import com.fasterxml.jackson.annotation.JsonProperty

data class From(
        @JsonProperty("name") var name: String?,
        @JsonProperty("time") var time: String?,
        @JsonProperty("tel") var tel: String?
)