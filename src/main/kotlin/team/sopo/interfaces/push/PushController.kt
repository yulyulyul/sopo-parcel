package team.sopo.interfaces.push

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.sopo.application.parcel.ParcelFacade
import team.sopo.domain.parcel.ParcelCommand
import javax.validation.Valid

@Validated
@RestController
@Tag(name = "SOPO 푸시 API - Test용")
@RequestMapping("/api/v1/sopo-parcel/test")
class PushController(private val parcelFacade: ParcelFacade) {

    @Operation(summary = "택배 push API")
    @PostMapping("/parcels/push")
    fun pushParcel(
        @RequestBody @Valid request: PushDto.PushParcelsRequest
    ): ResponseEntity<Unit> {
        parcelFacade.pushParcels(command = ParcelCommand.PushRequest(request.userToken!!, request.parcelIds!!))
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "택배 디바이스 깨우는 API")
    @PostMapping("/deviceAwaken")
    fun pushDeviceAwaken(
        @RequestBody @Valid request: PushDto.DeviceAwakenRequest
    ): ResponseEntity<Unit> {
        parcelFacade.pushDeviceAwaken(command = ParcelCommand.DeviceAwakenRequest(request.topic!!))
        return ResponseEntity.noContent().build()
    }
}
