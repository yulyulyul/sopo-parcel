package team.sopo.interfaces.parcel

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.hibernate.validator.constraints.Range
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import team.sopo.application.parcel.ParcelFacade
import team.sopo.common.annotation.CheckYYYYMM
import team.sopo.common.annotation.CheckYYYYMMPermitNull
import team.sopo.domain.parcel.ParcelCommand
import java.security.Principal
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.NotNull

@Validated
@RestController
@Tag(name = "SOPO 택배 관리 API")
@RequestMapping("/api/v1/sopo-parcel/delivery")
@PreAuthorize("hasRole('ROLE_USER')")
class DeliveryController(
    private val parcelFacade: ParcelFacade,
    private val parcelDtoMapper: ParcelDtoMapper
) {
    private val logger: Logger = LoggerFactory.getLogger(DeliveryController::class.java)

    @Operation(
        summary = "배송사 상태를 추가하는 API",
        security = [SecurityRequirement(name = "Authorization")]
    )
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/carrier/status")
    fun postCarrier(
        @RequestBody @Valid request: ParcelDto.RegisterCarrierRequest,
        principal: Principal
    ): ResponseEntity<Unit> {
        val command = ParcelCommand.RegisterCarrierStatus(request.carrier, request.available)
        parcelFacade.registerCarrierStatus(command)

        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "지원하는 배송사들 상태를 가져오는 API",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @GetMapping("/carriers/status")
    fun getCarrierStatusList(
        principal: Principal
    ): ResponseEntity<ApiResult<List<ParcelDto.CarrierStatus>>> {
        val carriers = parcelFacade.getCarrierStatusList()
        val result = ApiResult(data = parcelDtoMapper.ofDto(carriers))

        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "택배 Reporting API",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @PostMapping("/parcel/reporting")
    fun reportParcel(
        @RequestBody @Valid request: ParcelDto.ReportingRequest,
        principal: Principal
    ): ResponseEntity<Unit> {
        val command = ParcelCommand.Reporting(
            userToken = principal.name,
            parcelIds = request.parcelIds ?: throw ConstraintViolationException("* 택배 id를 확인해주세요.", mutableSetOf())
        )
        parcelFacade.reporting(command)

        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "단일 택배 조회 API",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @GetMapping("/parcel/{parcelId}")
    fun getParcel(
        @Parameter(name = "parcelId", description = "택배 id", required = true, example = "1")
        @PathVariable("parcelId", required = true)
        @NotNull(message = "* 택배 id를 확인해주세요.")
        parcelId: Long? = null,
        principal: Principal
    ): ResponseEntity<ParcelDto.Main> {
        logger.info("user : ${principal.name}")
        val command = ParcelCommand.GetParcel(
            userToken = principal.name,
            parcelId = parcelId ?: throw ConstraintViolationException("* 택배 id를 확인해주세요.", mutableSetOf())
        )
        val parcelInfo = parcelFacade.getParcel(command)
        return ResponseEntity.ok(parcelDtoMapper.of(parcelInfo))
    }

    @Operation(
        summary = "택배의 별칭을 수정하는 API",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @PatchMapping("/parcel/{parcelId}/alias")
    fun patchParcelAlias(
        @RequestBody @Valid request: ParcelDto.ChangeAliasRequest,
        @Parameter(name = "parcelId", description = "택배 id", required = true, example = "1")
        @PathVariable("parcelId", required = true)
        @NotNull(message = "* 택배 id를 확인해주세요.")
        parcelId: Long? = null,
        principal: Principal
    ): ResponseEntity<Unit> {

        val command = ParcelCommand.ChangeParcelAlias(principal.name, parcelId!!, request.alias)
        parcelFacade.changeParcelAlias(command)

        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "복수의 택배[배송중] 조회 API",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @GetMapping("/parcels/ongoing")
    fun getOngoingParcels(principal: Principal): ResponseEntity<List<ParcelDto.Main>> {

        val command = ParcelCommand.GetOngoingParcels(principal.name)
        val ongoings = parcelFacade.getOngoingParcels(command)

        return ResponseEntity.ok(parcelDtoMapper.of(ongoings))
    }

    @Operation(
        summary = "복수의 택배 조회 API",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @GetMapping("/parcels")
    fun getParcels(
        @Parameter(name = "parcel", description = "복수 조회할 택배 식별값 리스트", required = true)
        @RequestParam(value = "parcel", required = true)
        @NotNull(message = "* 택배 id를 확인해주세요.")
        parcels: List<Long>,
        principal: Principal
    ): ResponseEntity<List<ParcelDto.Main>> {

        val command = ParcelCommand.GetParcels(principal.name, parcels)
        val parcelInfos = parcelFacade.getParcels(command)

        return ResponseEntity.ok(parcelDtoMapper.of(parcelInfos))
    }

    @Operation(
        summary = "복수의 택배[배송완료] 조회(페이징 포함) API",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @GetMapping("/parcels/complete")
    fun getCompleteParcels(
        @Parameter(
            name = "page", description = "페이징 번호(0,1,2,3..)",
            required = true, `in` = ParameterIn.QUERY,
            schema = Schema(implementation = Int::class, example = "1")
        )
        @NotNull(message = "* 페이징 번호를 확인해주세요.")
        pageable: Pageable,
        @Parameter(
            name = "itemCnt", description = "아이템 조회 개수",
            required = true, `in` = ParameterIn.QUERY,
            schema = Schema(implementation = Integer::class, example = "10")
        )
        @NotNull(message = "* 아이템 개수를 확인해주세요.")
        @Range(message = "* 아이템 개수는 0-20 사이로 입력해주세요.", min = 0, max = 20)
        itemCnt: Int,
        @Parameter(
            name = "inquiryDate", description = "조회 날짜(202103 - 년월)",
            required = true, `in` = ParameterIn.QUERY,
            schema = Schema(implementation = String::class, example = "202006")
        )
        @NotNull(message = "* 조회 날짜를 확인해주세요.")
        @CheckYYYYMM(message = "* 조회 날짜의 형식을 확인해주세요.")
        inquiryDate: String,
        principal: Principal
    ): ResponseEntity<List<ParcelDto.Main>> {

        val command = ParcelCommand.GetCompleteParcels(principal.name, inquiryDate, itemCnt, pageable)
        val completes = parcelFacade.getCompleteParcels(command)

        return ResponseEntity.ok(parcelDtoMapper.of(completes))
    }

    @Operation(
        summary = "사용자의 조회 가능한 '년/월' 리스트 API",
        security = [SecurityRequirement(name = "Authorization")],
        deprecated = true
    )
    @Deprecated("getMonthsV2 사용 바람")
    @GetMapping("/parcels/months")
    fun getMonths(principal: Principal): ResponseEntity<List<ParcelDto.MonthlyParcelCntResponse>> {

        val command = ParcelCommand.GetMonthlyParcelCnt(principal.name)
        val monthlyParcelCnt = parcelFacade.getMonthlyParcelCnt(command)
        return ResponseEntity.ok(parcelDtoMapper.toResponse(monthlyParcelCnt))
    }

    @Operation(
        summary = "월별 완료된 택배 page info",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @GetMapping("/parcels/complete/monthly-page-info")
    fun getMonthsV2(
        @Parameter(
            name = "cursorDate", description = "조회 날짜 (202103 - 년월)",
            required = false, `in` = ParameterIn.QUERY,
            schema = Schema(implementation = String::class, example = "202103")
        )
        @CheckYYYYMMPermitNull(message = "* 조회 날짜의 형식을 확인해주세요.")
        cursorDate: String?,
        principal: Principal
    ): ResponseEntity<ParcelDto.MonthlyPageInfoResponse> {

        val command = ParcelCommand.GetMonthlyPageInfo(principal.name, cursorDate?.trim())
        val monthV2 = parcelFacade.getMonthlyPageInfo(command)
        return ResponseEntity.ok(parcelDtoMapper.toResponse(monthV2))
    }

    @Operation(
        summary = "단일 택배 상태 업데이트 API",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @PostMapping("/parcel/refresh")
    fun postParcelRefresh(
        @RequestBody @Valid request: ParcelDto.RefreshParcelRequest,
        principal: Principal
    ): ResponseEntity<ParcelDto.RefreshResponse> {

        val command = ParcelCommand.SingleRefresh(principal.name, request.parcelId!!)
        val singleRefresh = parcelFacade.singleRefresh(command)

        return ResponseEntity.ok(parcelDtoMapper.toResponse(singleRefresh))
    }

    @Operation(
        summary = "모든 택배 업데이트 API",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @PostMapping("/parcels/refresh")
    fun postParcelsRefresh(principal: Principal): ResponseEntity<Unit> {
        parcelFacade.entireRefresh(ParcelCommand.EntireRefresh(principal.name))
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "단일 택배 등록 API", security = [SecurityRequirement(name = "Oauth2", scopes = ["read", "write"])])
    @PostMapping("/parcel")
    fun postParcel(
        @RequestBody @Valid request: ParcelDto.RegisterParcelRequest,
        principal: Principal
    ): ResponseEntity<Long> {
        val registerCommand = request.toCommand(principal.name)
        val parcelInfo = parcelFacade.registerParcel(registerCommand)

        return ResponseEntity(parcelInfo.parcelId, HttpStatus.CREATED)
    }

    @Operation(
        summary = "택배 삭제 API",
        security = [SecurityRequirement(name = "Authorization")]
    )
    @DeleteMapping("/parcels")
    fun deleteParcels(
        @Parameter(name = "parcelIds", description = "삭제할 택배 식별값 리스트", required = true)
        @RequestBody @Valid request: ParcelDto.DeleteParcelsRequest,
        principal: Principal
    ): ResponseEntity<Unit> {
        val deleteCommand = ParcelCommand.DeleteParcel(principal.name, request.parcelIds!!)
        parcelFacade.deleteParcel(deleteCommand)

        return ResponseEntity.noContent().build()
    }
}
