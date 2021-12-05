package team.sopo.parcel.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import team.sopo.common.annotation.DateFormatYearMonth
import team.sopo.common.model.api.ApiResult
import team.sopo.parcel.ParcelInfo
import team.sopo.parcel.application.ParcelFacade
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.presentation.request.*
import java.security.Principal
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.NotNull

@Validated
@RestController
@Tag(name = "SOPO 택배 관리 API")
@RequestMapping("/api/v1/sopo-parcel/delivery")
@PreAuthorize("hasRole('ROLE_USER')")
class ParcelController(private val parcelFacade: ParcelFacade) {
    private val logger: Logger = LoggerFactory.getLogger(ParcelController::class.java)

    @Operation(
        summary = "단일 택배 조회 API",
        security = [SecurityRequirement(
            name = "Oauth2",
            scopes = ["read", "write"]
        ), SecurityRequirement(name = "BearerToken")]
    )
    @GetMapping("/parcel/{parcelId}")
    fun getParcel(
        @Parameter(name = "parcelId", description = "택배 id", required = true, example = "1")
        @PathVariable("parcelId", required = true)
        @NotNull(message = "* 택배 id를 확인해주세요.")
        parcelId: Long? = null,
        principal: Principal
    ): ResponseEntity<ApiResult<ParcelInfo.Main>> {
        logger.info("user : ${principal.name}")
        val command = ParcelCommand.GetParcel(
            userId = principal.name,
            parcelId = parcelId ?: throw ConstraintViolationException("* 택배 id를 확인해주세요.", mutableSetOf())
        )
        val parcel = parcelFacade.retrieveParcel(command)
        val result = ApiResult(data = parcel)
        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "택배의 별칭을 수정하는 API",
        security = [SecurityRequirement(
            name = "Oauth2",
            scopes = ["read", "write"]
        ), SecurityRequirement(name = "BearerToken")]
    )
    @PatchMapping("/parcel/{parcelId}/alias")
    fun patchParcelAlias(
        @RequestBody @Valid request: ChangeAliasRequest,
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
        security = [SecurityRequirement(
            name = "Oauth2",
            scopes = ["read", "write"]
        ), SecurityRequirement(name = "BearerToken")]
    )
    @GetMapping("/parcels/ongoing")
    fun getOngoingParcels(principal: Principal): ResponseEntity<ApiResult<List<ParcelInfo.Main>>> {

        val command = ParcelCommand.GetOngoingParcels(principal.name)
        val ongoings = parcelFacade.retrieveOngoingParcels(command)
        val result = ApiResult(data = ongoings)

        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "복수의 택배[배송완료] 조회(페이징 포함) API",
        security = [SecurityRequirement(
            name = "Oauth2",
            scopes = ["read", "write"]
        ), SecurityRequirement(name = "BearerToken")]
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
            name = "inquiryDate", description = "조회 날짜(202103 - 년월)",
            required = true, `in` = ParameterIn.QUERY,
            schema = Schema(implementation = String::class, example = "202006")
        )
        @NotNull(message = "* 조회 날짜를 확인해주세요.")
        @DateFormatYearMonth(message = "* 조회 날짜의 형식을 확인해주세요.")
        inquiryDate: String,
        principal: Principal
    ): ResponseEntity<ApiResult<List<ParcelInfo.Main>>> {

        val command = ParcelCommand.GetCompleteParcels(principal.name, inquiryDate, pageable)
        val completes = parcelFacade.retrieveCompleteParcels(command)
        val result = ApiResult(data = completes)

        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "사용자의 조회 가능한 '년/월' 리스트 API",
        security = [SecurityRequirement(
            name = "Oauth2",
            scopes = ["read", "write"]
        ), SecurityRequirement(name = "BearerToken")]
    )
    @GetMapping("/parcels/months")
    fun getMonths(principal: Principal): ResponseEntity<ApiResult<List<ParcelInfo.MonthlyParcelCnt>>> {

        val command = ParcelCommand.GetMonthlyParcelCnt(principal.name)
        val monthlyParcelCnt = parcelFacade.retrieveMonthlyParcelCnt(command)
        val successResult = ApiResult(data = monthlyParcelCnt)
        return ResponseEntity.ok(successResult)
    }

    @Operation(
        summary = "단일 택배 상태 업데이트 API",
        security = [SecurityRequirement(
            name = "Oauth2",
            scopes = ["read", "write"]
        ), SecurityRequirement(name = "BearerToken")]
    )
    @PostMapping("/parcel/refresh")
    fun postParcelRefresh(
        @RequestBody @Valid request: RefreshParcelRequest,
        principal: Principal
    ): ResponseEntity<ApiResult<ParcelInfo.RefreshedParcel>> {

        val command = ParcelCommand.SingleRefresh(principal.name, request.parcelId!!)
        val singleRefresh = parcelFacade.singleRefresh(command)

        return ResponseEntity.ok(ApiResult(data = singleRefresh))
    }

    @Operation(
        summary = "모든 택배 업데이트 API",
        security = [SecurityRequirement(name = "Oauth2", scopes = ["read", "write"]),
            SecurityRequirement(name = "BearerToken")]
    )
    @PostMapping("/parcels/refresh")
    fun postParcelsRefresh(principal: Principal): ResponseEntity<ApiResult<String>> {
        parcelFacade.entireRefresh(ParcelCommand.EntireRefresh(principal.name))
        return ResponseEntity.ok(ApiResult(data = ""))
    }

    @Operation(summary = "단일 택배 등록 API", security = [SecurityRequirement(name = "Oauth2", scopes = ["read", "write"])])
    @PostMapping("/parcel")
    fun postParcel(
        @RequestBody @Valid request: RegisterParcelRequest,
        principal: Principal
    ): ResponseEntity<ApiResult<Long>> {

        val registerCommand = request.toCommand(principal.name)
        val parcelInfo = parcelFacade.registerParcel(registerCommand)

        return ResponseEntity(ApiResult(data = parcelInfo.parcelId), HttpStatus.CREATED)
    }

    @Operation(
        summary = "택배 삭제 API",
        security = [SecurityRequirement(
            name = "Oauth2",
            scopes = ["read", "write"]
        ), SecurityRequirement(name = "BearerToken")]
    )
    @DeleteMapping("/parcels")
    fun deleteParcels(
        @Parameter(name = "parcelIds", description = "삭제할 택배 식별값 리스트", required = true)
        @RequestBody @Valid request: DeleteParcelsRequest,
        principal: Principal
    ): ResponseEntity<Unit> {
        val deleteCommand = ParcelCommand.DeleteParcel(principal.name, request.parcelIds!!)
        parcelFacade.deleteParcel(deleteCommand)

        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "택배 push API")
    @PostMapping("/parcels/push")
    fun pushParcel(
        @Parameter(name = "parcelIds", description = "업데이트할 택배id 리스트", required = true)
        @RequestBody @Valid request: PushParcelsRequest
    ): ResponseEntity<Unit> {
        parcelFacade.pushParcels(command = ParcelCommand.PushRequest(request.userId!!, request.parcelIds!!))
        return ResponseEntity.noContent().build()
    }
}
