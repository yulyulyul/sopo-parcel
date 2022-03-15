package team.sopo.infrastructure.parcel.tracker.delivery

import com.fasterxml.jackson.annotation.JsonProperty

data class State(
    @JsonProperty("id") var id: String,
    @JsonProperty("text") var text: String
)