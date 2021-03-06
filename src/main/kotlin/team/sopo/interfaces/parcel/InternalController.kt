package team.sopo.interfaces.parcel

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.sopo.domain.parcel.ParcelInfo
import team.sopo.application.parcel.ParcelFacade
import team.sopo.domain.parcel.ParcelCommand
import javax.validation.constraints.NotNull

@RestController
@Tag(name = "SOPO 택배 INTERNAL API")
@RequestMapping("/internal")
class InternalController(private val parcelFacade: ParcelFacade) {
    @Operation(summary = "유저의 사용 정보를 조회하는 API")
    @GetMapping("/parcel/usage-info/{userToken}")
    fun getServiceUsageInfo(
        @PathVariable("userToken", required = true)
        @NotNull(message = "* userToken를 확인해주세요.")
        userToken: String? = null
    ): ResponseEntity<ParcelInfo.ParcelUsage> {
        val usageInfo = parcelFacade.getUsageInfo(ParcelCommand.GetUsageInfo(userToken!!))
        return ResponseEntity.ok(usageInfo)
    }
}