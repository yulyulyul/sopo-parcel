package team.sopo.parcel.domain.trackinginfo

import com.fasterxml.jackson.annotation.JsonProperty

data class Location(
        @JsonProperty("name") val name: String
)