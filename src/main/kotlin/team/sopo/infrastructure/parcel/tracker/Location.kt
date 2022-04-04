package team.sopo.infrastructure.parcel.tracker

import com.fasterxml.jackson.annotation.JsonProperty

class Location(
    @JsonProperty("name") val name: String
)