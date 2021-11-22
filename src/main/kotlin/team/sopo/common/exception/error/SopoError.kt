package team.sopo.common.exception.error

import org.springframework.http.HttpStatus

enum class SopoError(
    val status: HttpStatus,
    val type: ErrorType,
    val code: Int,
    val message: String?) {

    // Common
    AUTHORIZE_FAIL(HttpStatus.FORBIDDEN, ErrorType.AUTHORIZE, 101, "허가되지 않은 접근 입니다."),
    AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, ErrorType.AUTHENTICATION, 102, "인증에 실패한 유저입니다."),
    VALIDATION(HttpStatus.BAD_REQUEST, ErrorType.VALIDATION, 103, null),

    INSUFFICIENT_CONDITION(HttpStatus.CONFLICT, ErrorType.CONFLICT, 105, null),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.UNKNOWN_ERROR, 199,"현재 서비스를 이용할 수 없습니다. 다음에 다시 시도해주세요."),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.SYSTEM, 999, null),

    // Oauth2
    OAUTH2_INVALID_CLIENT(HttpStatus.UNAUTHORIZED, ErrorType.OAUTH2, 801, "Client 인증에 실패했습니다."),
    OAUTH2_UNAUTHORIZED_CLIENT(HttpStatus.BAD_REQUEST, ErrorType.OAUTH2, 802, "해당 Client가 Resource에 대한 접근권한이 존재하지 않습니다."),
    OAUTH2_INVALID_GRANT(HttpStatus.BAD_REQUEST, ErrorType.OAUTH2, 803, "유효하지 않은 접근입니다."),
    OAUTH2_INVALID_SCOPE(HttpStatus.FORBIDDEN, ErrorType.OAUTH2, 804, "지정한 Scope 설정이 잘 못 되었습니다."),
    OAUTH2_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, ErrorType.OAUTH2, 805, "유효하지 않은 토큰 입니다."),
    OAUTH2_INVALID_REQUEST(HttpStatus.BAD_REQUEST, ErrorType.OAUTH2, 806, "유효하지 않은 요청입니다."),
    OAUTH2_REDIRECT_URI_MISMATCH(HttpStatus.BAD_REQUEST, ErrorType.OAUTH2, 807, "등록된 Redirect url과 일치하지 않습니다."),
    OAUTH2_UNSUPPORTED_GRANT_TYPE(HttpStatus.BAD_REQUEST, ErrorType.OAUTH2, 808, "Grant_type 설정이 잘 못 되었습니다."),
    OAUTH2_UNSUPPORTED_RESPONSE_TYPE(HttpStatus.BAD_REQUEST, ErrorType.OAUTH2, 809, "지원하지 않는 응답 타입 입니다."),
    OAUTH2_ACCESS_DENIED(HttpStatus.FORBIDDEN, ErrorType.OAUTH2, 810, "허가되지 않은 접근입니다."),
    OAUTH2_UNKNOWN(HttpStatus.UNAUTHORIZED, ErrorType.OAUTH2, 811, "토큰 인증에 실패하였습니다."),

    // Specific
    ALREADY_REGISTERED_PARCEL(HttpStatus.CONFLICT, ErrorType.CONFLICT, 701,"이미 등록된 택배입니다."),
    OVER_REGISTERED_PARCEL(HttpStatus.CONFLICT, ErrorType.CONFLICT, 702,"등록할 수 있는 택배의 개수를 초과하였습니다."),
    PARCEL_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorType.NO_RESOURCE, 703, null),
    FAIL_TO_SEARCH_PARCEL(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.DELIVERY, 704, null),
}