package team.sopo.domain.parcel.trackinginfo

import com.fasterxml.jackson.annotation.JsonProperty

data class Location(
        @JsonProperty("name") val name: String
)