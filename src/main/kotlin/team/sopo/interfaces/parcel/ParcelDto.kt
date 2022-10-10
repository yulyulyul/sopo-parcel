package team.sopo.interfaces.parcel

import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.Length
import team.sopo.common.annotation.Enum
import team.sopo.domain.parcel.carrier.Carrier
import team.sopo.domain.parcel.Parcel
import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.trackinginfo.TrackingInfo
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class ParcelDto {

    data class ChangeAliasRequest(
        @field: NotBlank(message = "택배 별칭을 확인해주세요.")
        @field: Length(max = 25, message = "택배 별칭(은)는 25글자를 초과할 수 없습니다.")
        val alias: String = ""
    )

    data class ReportingRequest(
        @field: NotNull(message = "* 리포팅할 택배 목록을 확인해주세요.")
        val parcelIds: MutableList<Long>? = null
    )

    data class DeleteParcelsRequest(
        @field: NotNull(message = "* 삭제할 택배 목록을 확인해주세요.")
        val parcelIds: MutableList<Long>? = null
    )

    data class RefreshParcelRequest(
        @NotNull(message = "* 택배 id를 확인해주세요.")
        val parcelId: Long? = null
    )

    data class RegisterParcelRequest(
        @field: NotNull(message = "배송사를 확인해주세요.")
        @field: Enum(enumClass = Carrier::class, message = "지원하지 않은 배송사입니다.", ignoreCase = true)
        val carrier: String? = null,

        @field: NotBlank(message = "송장 번호의 길이가 0 입니다.")
        @field: Length(max = 15, message = "송장 번호(은)는 15글자를 초과할 수 없습니다.")
        val waybillNum: String = "",

        @field: Length(max = 25, message = "택배 별칭(은)는 25글자를 초과할 수 없습니다.")
        val alias: String = ""
    ) {
        fun toCommand(userToken: String): ParcelCommand.RegisterParcel {
            return ParcelCommand.RegisterParcel(userToken, Carrier.valueOf(carrier!!).CODE, waybillNum, alias)
        }
    }

    class RegisterCarrierRequest(
        @field: NotBlank(message = "배송사를 확인해주세요.")
        @field: Enum(enumClass = Carrier::class, message = "지원하지 않은 배송사입니다.", ignoreCase = true)
        val carrier: String="",
        @field: NotBlank(message = "배송사 이름을 확인해주세요.")
        val name: String="",
        val available: Boolean=true
    )


    @Schema(name = "택배 리턴 모델")
    class Main(
        @Schema(name = "택배 id", example = "1")
        var parcelId: Long,
        @Schema(name = "유저 id", example = "sopo@sooopo.com")
        var userId: Long,
        @Schema(name = "송장번호", example = "123456789")
        var waybillNum: String,
        @Schema(name = "배송사", example = "LOGEN")
        var carrier: Carrier,
        @Schema(name = "택배 별칭", example = "사과")
        var alias: String,
        @Schema(name = "택배 배송 상황")
        var inquiryResult: TrackingInfo? = null,
        @Schema(name = "택배 배송 상황 해쉬값")
        var inquiryHash: String? = null,
        @Schema(name = "배송 상태", example = "delivered")
        var deliveryStatus: Parcel.DeliveryStatus? = null,
        @Schema(name = "등록 날짜", example = "2021-06-23")
        var regDte: LocalDateTime,
        @Schema(name = "도착 날짜", example = "2021-06-23")
        var arrivalDte: LocalDateTime? = null,
        @Schema(name = "데이터 수정 날짜", example = "2021-06-23")
        var auditDte: LocalDateTime,
        @Schema(name = "택배 데이터의 상태", example = "1")
        var status: Int,
        @Schema(name = "택배 Reporting", example = "false")
        var reported: Boolean
    )


    class RefreshResponse(
        val parcel: Main,
        val isUpdated: Boolean
    )

    class ParcelUsageResponse(
        val registeredCountIn2Week: Long,
        val totalRegisteredCount: Long
    )

    class MonthlyParcelCntResponse(_time: String, _count: Long) {
        val time = _time
        val count = _count

        override fun toString(): String {
            return "{time:$time, count:$count}"
        }
    }

    data class MonthlyPageInfoResponse(
        val hasPrevious: Boolean,
        val previousDate: String?,
        val hasNext: Boolean,
        val nextDate: String?,
        val cursorDate: String?
    )

    data class CarrierStatus(
        val carrier: String,
        val name: String,
        val available: Boolean
    )
}