package team.sopo.parcel

import io.swagger.v3.oas.annotations.media.Schema
import team.sopo.parcel.domain.Carrier
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.trackinginfo.TrackingInfo
import java.time.LocalDateTime

class ParcelInfo {
        @Schema(name = "택배 리턴 모델")
        class Main(
            @Schema(name = "택배 id", example = "1")
            var parcelId: Long? = null,
            @Schema(name = "유저 id", example = "sopo@sooopo.com")
            var userId: Long,
            @Schema(name = "송장번호", example = "123456789")
            var waybillNum: String? = null,
            @Schema(name = "배송사", example = "LOGEN")
            var carrier: Carrier? = null,
            @Schema(name = "택배 별칭", example = "사과")
            var alias: String? = null,
            @Schema(name = "택배 배송 상황")
            var inquiryResult: TrackingInfo? = null,
            @Schema(name = "택배 배송 상황 해쉬값")
            var inquiryHash: String? = null,
            @Schema(name = "배송 상태", example = "delivered")
            var deliveryStatus: Parcel.DeliveryStatus? = null,
            @Schema(name="등록 날짜", example = "2021-06-23")
            var regDte: LocalDateTime? = null,
            @Schema(name = "도착 날짜", example = "2021-06-23")
            var arrivalDte: LocalDateTime? = null,
            @Schema(name = "데이터 수정 날짜", example = "2021-06-23")
            var auditDte: LocalDateTime? = null,
            @Schema(name = "택배 데이터의 상태", example = "1")
            var status: Int? = null
        )

    data class RefreshedParcel(
        val parcel: Main,
        val isUpdated: Boolean
    )

    data class ParcelUsage(
        val registeredCountIn2Week: Long,
        val totalRegisteredCount: Long
    )

    class MonthlyParcelCnt(_time: String, _count: Long){
        val time = _time
        val count = _count

        override fun toString(): String {
            return "{time:$time, count:$count}"
        }
    }
}