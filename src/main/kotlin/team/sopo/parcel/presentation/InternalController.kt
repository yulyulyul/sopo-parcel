package team.sopo.parcel.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.sopo.parcel.ParcelInfo
import team.sopo.parcel.application.ParcelFacade
import team.sopo.parcel.domain.ParcelCommand
import javax.validation.constraints.NotNull

@RestController
@Tag(name = "SOPO 택배 INTERNAL API")
@RequestMapping("/internal")
class InternalController(private val parcelFacade: ParcelFacade) {
    @Operation(summary = "유저의 사용 정보를 조회하는 API")
    @GetMapping("/parcel/usage-info/{userId}")
    fun getServiceUsageInfo(
        @PathVariable("userId", required = true)
        @NotNull(message = "* 유저 id를 확인해주세요.")
        userId: Long? = null
    ): ResponseEntity<ParcelInfo.ParcelUsage> {
        val usageInfo = parcelFacade.getUsageInfo(ParcelCommand.GetUsageInfo(userId!!))
        return ResponseEntity.ok(usageInfo)
    }
}