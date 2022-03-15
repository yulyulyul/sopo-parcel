package team.sopo.domain.parcel.trackinginfo

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "TrackingInfo", description = "")
data class TrackingInfo(
    @Schema(name = "발송하는 사람에 대한 정보", required = true)
    @JsonProperty("from") val from: From?,

    @Schema(name = "수신하는 사람에 대한 정보", required = true)
    @JsonProperty("to") val to: To?,

    @Schema(name = "배송 상태", required = true)
    @JsonProperty("state") val state: State,

    @Schema(name = "택배명", required = true)
    @JsonProperty("item") val item: String?,

    @Schema(name = "택배 배송 과정", required = true)
    @JsonProperty("progresses") var progresses: MutableList<Progresses?>,

    @Schema(name = "택배 배송 회사", required = true)
    @JsonProperty("carrier") val carrier: Carrier?
)