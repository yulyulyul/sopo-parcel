package team.sopo.parcel.domain.vo.deliverytracker

import com.fasterxml.jackson.annotation.JsonProperty

data class From(
        @JsonProperty("name") var name: String?,
        @JsonProperty("time") var time: String?
)