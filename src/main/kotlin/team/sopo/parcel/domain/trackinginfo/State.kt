package team.sopo.parcel.domain.trackinginfo

import com.fasterxml.jackson.annotation.JsonProperty

data class State (
        @JsonProperty("id") var id: String,
        @JsonProperty("text") var text: String
)