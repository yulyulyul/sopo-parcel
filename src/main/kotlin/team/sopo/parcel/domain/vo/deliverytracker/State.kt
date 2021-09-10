package team.sopo.parcel.domain.vo.deliverytracker

import com.fasterxml.jackson.annotation.JsonProperty

data class State (
        @JsonProperty("id") var id: String,
        @JsonProperty("text") var text: String
)