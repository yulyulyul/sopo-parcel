package team.sopo.parcel.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.sopo.parcel.application.ParcelFacade
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.presentation.request.DeviceAwakenRequest
import team.sopo.parcel.presentation.request.PushParcelsRequest
import javax.validation.Valid

@Validated
@RestController
@Tag(name = "SOPO 푸시 API - Test용")
@RequestMapping("/api/v1/sopo-parcel/test")
class PushController(private val parcelFacade: ParcelFacade) {

    @Operation(summary = "택배 push API")
    @PostMapping("/parcels/push")
    fun pushParcel(
        @RequestBody @Valid request: PushParcelsRequest
    ): ResponseEntity<Unit> {
        parcelFacade.pushParcels(command = ParcelCommand.PushRequest(request.userId!!, request.parcelIds!!))
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "택배 디바이스 깨우는 API")
    @PostMapping("/deviceAwaken")
    fun pushDeviceAwaken(
        @RequestBody @Valid request: DeviceAwakenRequest
    ): ResponseEntity<Unit> {
        parcelFacade.pushDeviceAwaken(command = ParcelCommand.DeviceAwakenRequest(request.topic!!))
        return ResponseEntity.noContent().build()
    }
}
