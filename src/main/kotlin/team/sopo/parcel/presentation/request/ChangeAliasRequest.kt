package team.sopo.parcel.presentation.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ChangeAliasRequest(
    @field: NotBlank(message = "택배 별칭을 확인해주세요.")
    @field: Length(max = 25, message = "택배 별칭(은)는 25글자를 초과할 수 없습니다.")
    val alias: String = ""
)