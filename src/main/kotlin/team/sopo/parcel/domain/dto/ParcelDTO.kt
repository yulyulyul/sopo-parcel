package team.sopo.parcel.domain.dto

import team.sopo.parcel.domain.Carrier
import team.sopo.parcel.domain.DeliveryStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(name = "택배 모델")
data class ParcelDTO(
    @Schema(name = "택배 id", example = "1")
        var parcelId: Long? = null,
    @Schema(name = "유저 id", example = "sopo@sooopo.com")
        var userId: String? = null,
    @Schema(name = "송장번호", example = "123456789")
        var waybillNum: String? = null,
    @Schema(name = "배송사", example = "LOGEN")
        var carrier: Carrier? = null,
    @Schema(name = "택배 별칭", example = "사과")
        var alias: String? = null,
    @Schema(name = "택배 배송 상황")
        var inquiryResult: String? = null,
    @Schema(name = "택배 배송 상황 해쉬값")
        var inquiryHash: String? = null,
    @Schema(name = "배송 상태", example = "delivered")
        var deliveryStatus: DeliveryStatus? = null,
    @Schema(name="등록 날짜", example = "2021-06-23")
        var regDt: LocalDate? = null,
    @Schema(name = "도착 날짜", example = "2021-06-23")
        var arrivalDte: LocalDateTime? = null,
    @Schema(name = "데이터 수정 날짜", example = "2021-06-23")
        var auditDte: LocalDateTime? = null,
    @Schema(name = "택배 데이터의 상태", example = "1")
        var status: Int? = null
)