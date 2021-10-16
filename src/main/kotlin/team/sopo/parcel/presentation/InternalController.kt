package team.sopo.parcel.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.sopo.common.model.api.ApiResult
import team.sopo.parcel.application.GetUsageInfoService
import team.sopo.parcel.domain.dto.UsageInfoDTO
import javax.validation.ConstraintViolationException
import javax.validation.constraints.NotNull

@RestController
@Tag(name = "SOPO 택배 INTERNAL API")
@RequestMapping("/internal")
class InternalController(private val getUsageInfoService: GetUsageInfoService) {
    @Operation(summary = "유저의 사용 정보를 조회하는 API")
    @GetMapping("/parcel/usage-info/{userId}")
    fun getServiceUsageInfo(
        @PathVariable("userId", required = true)
        @NotNull(message = "* 유저 id를 확인해주세요.")
        userId: String? = null
    ):ResponseEntity<ApiResult<UsageInfoDTO>>{
        val usageInfoDto = getUsageInfoService.getUsageInfo(userId ?: throw ConstraintViolationException("* userId를 확인해주세요.", mutableSetOf()))
        return ResponseEntity.ok(ApiResult(data = usageInfoDto))
    }
}