package team.sopo.infrastructure.parcel.tracker

import com.fasterxml.jackson.annotation.JsonProperty

data class State(
    @JsonProperty("id") var id: String,
    @JsonProperty("text") var text: String
)