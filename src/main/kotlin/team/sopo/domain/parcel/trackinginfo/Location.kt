package team.sopo.domain.parcel.trackinginfo

import com.fasterxml.jackson.annotation.JsonProperty

class Location(
    @JsonProperty("name") val name: String
)