package team.sopo.common.exception

import team.sopo.common.enums.ResponseEnum
import net.minidev.json.JSONObject
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception

class CustomOauthException : OAuth2Exception{

    var oauthErrMsg: String
    var oauthErrCode: String
    var responseEnum: ResponseEnum
    var additionalData: String? = null

    val detailMsg: String
        get() {
            val jsonObj = JSONObject().apply {
                put("errorMsg", oauthErrMsg)
            }
            if(oauthErrCode.isEmpty()){
                jsonObj["errorCode"] = responseEnum.MSG
            }
            else{
                jsonObj["errorCode"] = oauthErrMsg
            }
            return jsonObj.toJSONString()
        }

    constructor(oauthErrMsg: String, oauthErrCode: String):super(oauthErrMsg){
        this.oauthErrMsg = oauthErrMsg
        this.oauthErrCode = oauthErrCode
        this.responseEnum = getResponseEnum(oauthErrCode)
    }

    constructor(oauthErrMsg: String, oauthErrCode: String = "", responseEnum: ResponseEnum):super(oauthErrMsg){
        this.oauthErrMsg = oauthErrMsg
        this.oauthErrCode = oauthErrCode
        this.responseEnum = responseEnum
    }

    fun setData(data: String?){
        this.additionalData = data
    }

    private fun getResponseEnum(errorCode: String): ResponseEnum {
        return when {
            INVALID_CLIENT == errorCode -> {
                ResponseEnum.TOKEN_ERROR_INVALID_CLIENT
            }
            UNAUTHORIZED_CLIENT == errorCode -> {
                ResponseEnum.TOKEN_ERROR_UNAUTHORIZED_CLIENT
            }
            INVALID_GRANT == errorCode -> {
                ResponseEnum.TOKEN_ERROR_INVALID_GRANT
            }
            INVALID_SCOPE == errorCode -> {
                ResponseEnum.TOKEN_ERROR_INVALID_SCOPE
            }
            INVALID_TOKEN == errorCode -> {
                ResponseEnum.TOKEN_ERROR_INVALID_TOKEN
            }
            INVALID_REQUEST == errorCode -> {
                ResponseEnum.TOKEN_ERROR_INVALID_REQUEST
            }
            REDIRECT_URI_MISMATCH == errorCode -> {
                ResponseEnum.TOKEN_ERROR_REDIRECT_URI_MISMATCH
            }
            UNSUPPORTED_GRANT_TYPE == errorCode -> {
                ResponseEnum.TOKEN_ERROR_UNSUPPORTED_GRANT_TYPE
            }
            UNSUPPORTED_RESPONSE_TYPE == errorCode -> {
                ResponseEnum.TOKEN_ERROR_UNSUPPORTED_RESPONSE_TYPE
            }
            ACCESS_DENIED == errorCode -> {
                ResponseEnum.TOKEN_ERROR_ACCESS_DENIED
            }
            "ALREADY_LOGGED_IN" == errorCode ->{
                ResponseEnum.TOKEN_ERROR_ALREADY_LOGGED_IN
            }
            "DEVICE_INFO_IS_EMPTY" == errorCode ->{
                ResponseEnum.TOKEN_ERROR_DEVICE_INFO_IS_EMPTY
            }
            else -> {
                ResponseEnum.TOKEN_ERROR_UNKNOWN
            }
        }
    }
}