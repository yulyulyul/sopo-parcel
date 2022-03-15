package team.sopo.common.exception

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception
import team.sopo.common.exception.error.SopoError

class SopoOauthException: OAuth2Exception{
    private val oauthErrMsg: String
    private val oauthErrCode: String

    val sopoError: SopoError
    var additionalData: String? = null

    constructor(oauthErrMsg: String, oauthErrCode: String) : super(oauthErrMsg) {
        this.oauthErrMsg = oauthErrMsg
        this.oauthErrCode = oauthErrCode
        this.sopoError = getSopoError(oauthErrCode)
    }

    constructor(oauthErrMsg: String, oauthErrCode: String, sopoErrorCode: Int) : super(oauthErrMsg){
        this.oauthErrMsg = oauthErrMsg
        this.oauthErrCode = oauthErrCode
        this.sopoError = getSopoError(sopoErrorCode)
    }

    private fun getSopoError(sopoErrorCode: Int): SopoError {
        return when(sopoErrorCode){
            SopoError.OAUTH2_INVALID_CLIENT.code -> SopoError.OAUTH2_INVALID_CLIENT
            SopoError.OAUTH2_UNAUTHORIZED_CLIENT.code -> SopoError.OAUTH2_UNAUTHORIZED_CLIENT
            SopoError.OAUTH2_INVALID_GRANT.code -> SopoError.OAUTH2_INVALID_GRANT
            SopoError.OAUTH2_INVALID_SCOPE.code -> SopoError.OAUTH2_INVALID_SCOPE
            SopoError.OAUTH2_INVALID_TOKEN.code -> SopoError.OAUTH2_INVALID_TOKEN
            SopoError.OAUTH2_INVALID_REQUEST.code -> SopoError.OAUTH2_INVALID_REQUEST
            SopoError.OAUTH2_REDIRECT_URI_MISMATCH.code -> SopoError.OAUTH2_REDIRECT_URI_MISMATCH
            SopoError.OAUTH2_UNSUPPORTED_GRANT_TYPE.code -> SopoError.OAUTH2_UNSUPPORTED_GRANT_TYPE
            SopoError.OAUTH2_UNSUPPORTED_RESPONSE_TYPE.code -> SopoError.OAUTH2_UNSUPPORTED_RESPONSE_TYPE
            SopoError.OAUTH2_ACCESS_DENIED.code -> SopoError.OAUTH2_ACCESS_DENIED
            SopoError.OAUTH2_DELETED_TOKEN.code -> SopoError.OAUTH2_DELETED_TOKEN
            else -> {
                SopoError.OAUTH2_UNKNOWN
            }
        }
    }

    private fun getSopoError(errorCode: String): SopoError {
        return when {
            INVALID_CLIENT == errorCode -> {
                SopoError.OAUTH2_INVALID_CLIENT
            }
            UNAUTHORIZED_CLIENT == errorCode -> {
                SopoError.OAUTH2_UNAUTHORIZED_CLIENT
            }
            INVALID_GRANT == errorCode -> {
                SopoError.OAUTH2_INVALID_GRANT
            }
            INVALID_SCOPE == errorCode -> {
                SopoError.OAUTH2_INVALID_SCOPE
            }
            INVALID_TOKEN == errorCode -> {
                SopoError.OAUTH2_INVALID_TOKEN
            }
            INVALID_REQUEST == errorCode -> {
                SopoError.OAUTH2_INVALID_REQUEST
            }
            REDIRECT_URI_MISMATCH == errorCode -> {
                SopoError.OAUTH2_REDIRECT_URI_MISMATCH
            }
            UNSUPPORTED_GRANT_TYPE == errorCode -> {
                SopoError.OAUTH2_UNSUPPORTED_GRANT_TYPE
            }
            UNSUPPORTED_RESPONSE_TYPE == errorCode -> {
                SopoError.OAUTH2_UNSUPPORTED_RESPONSE_TYPE
            }
            ACCESS_DENIED == errorCode -> {
                SopoError.OAUTH2_ACCESS_DENIED
            }
            else -> {
                SopoError.OAUTH2_UNKNOWN
            }
        }
    }
}