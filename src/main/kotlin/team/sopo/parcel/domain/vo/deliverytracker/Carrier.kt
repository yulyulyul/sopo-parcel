package team.sopo.parcel.domain.vo.deliverytracker

import com.fasterxml.jackson.annotation.JsonProperty

data class Carrier(
        @JsonProperty("id") val id: String?,
        @JsonProperty("name") val name: String?,
        @JsonProperty("tel") val tel: String?
)