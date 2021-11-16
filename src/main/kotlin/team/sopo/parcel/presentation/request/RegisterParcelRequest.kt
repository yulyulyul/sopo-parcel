package team.sopo.parcel.presentation.request

import team.sopo.parcel.domain.Carrier
import org.hibernate.validator.constraints.Length
import team.sopo.parcel.domain.ParcelCommand
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RegisterParcelRequest(
    @field: NotNull(message = "배송사를 확인해주세요.")
    val carrier: Carrier? = null,

    @field: NotBlank(message = "송장 번호의 길이가 0 입니다.")
    @field: Length(max = 15, message = "송장 번호(은)는 15글자를 초과할 수 없습니다.")
    val waybillNum: String = "",

    @field: Length(max = 25, message = "택배 별칭(은)는 25글자를 초과할 수 없습니다.")
    val alias: String = ""
) {
    fun toCommand(userId: String): ParcelCommand.RegisterParcel {
        return ParcelCommand.RegisterParcel(userId, carrier!!, waybillNum, alias)
    }
}