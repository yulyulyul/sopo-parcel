package team.sopo.common.config.oauth2

import com.google.gson.Gson
import team.sopo.common.exception.CustomOauthException
import team.sopo.common.model.api.ApiResult
import team.sopo.common.model.authentication.TokenError
import team.sopo.common.util.JsonUtil.mapToJson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.codehaus.jettison.json.JSONException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.security.crypto.codec.Base64
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.AccessTokenConverter
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.util.CollectionUtils
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.io.IOException
import java.io.UnsupportedEncodingException

/**
 *  기본으로 제공되는 RemoteTokenService도 있지만!
 *  SOPO 프로젝트에서는 인증서버에서 기존에 기본으로 제공하는 값과는 다르게
 *  응답 값을 일정한 양식(ApiResult.class)로 변형해서 던져주기 떄문에
 *  loadAuthentication을 SOPO에 맞게 변형해서 사용해야함.
 *
 *  따라서 아래의  CustomResourceServerTokenServices을 정의해서 RemoteTokenService를 정의해서 사용한다.
 * ( 기존의 것과 크게 다른 것은 없고 loadAuthentication만 좀 다름.)
 */
class CustomResourceServerTokenServices(
    private var checkTokenEndpointUrl: String,
    private var clientId: String,
    private var clientSecret: String
): ResourceServerTokenServices{
    val logger: Logger = LogManager.getLogger(this.javaClass.name)

    private var tokenConverter: AccessTokenConverter = DefaultAccessTokenConverter()
    private var restTemplate: RestOperations = RestTemplate()
    private var tokenName =  "token"

    init {
        (restTemplate as RestTemplate).errorHandler = object:  DefaultResponseErrorHandler() {
            @Throws(IOException::class)  // Ignore 400
            override fun handleError(response: ClientHttpResponse) {
                if (response.rawStatusCode != 400) {
                    super.handleError(response)
                }
            }
        }
    }

    fun setRestTemplate(restTemplate: RestOperations?) {
        this.restTemplate = restTemplate!!
    }
    fun setCheckTokenEndpointUrl(checkTokenEndpointUrl: String?) {
        this.checkTokenEndpointUrl = checkTokenEndpointUrl!!
    }

    fun setClientId(clientId: String) {
        this.clientId = clientId
    }

    fun setClientSecret(clientSecret: String) {
        this.clientSecret = clientSecret
    }

    fun setAccessTokenConverter(accessTokenConverter: AccessTokenConverter) {
        tokenConverter = accessTokenConverter
    }

    fun setTokenName(tokenName: String) {
        this.tokenName = tokenName
    }

    override fun loadAuthentication(accessToken: String?): OAuth2Authentication {
        val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
        formData.add(tokenName, accessToken)
        val headers = HttpHeaders()
        headers["Authorization"] = getAuthorizationHeader(clientId, clientSecret)

       val map = postForMap(checkTokenEndpointUrl, formData, headers)

        if (CollectionUtils.isEmpty(map) || map.isNullOrEmpty()) {
            if (logger.isDebugEnabled) {
                logger.debug("check_token returned empty")
            }
            throw InvalidTokenException(accessToken)
        }
        try {
            val jsonStr = mapToJson(map)

            // apiResult로 변환이 된다는 것은 토큰 관련 무슨 문제가 있다는 뜻이다.
            val apiResult = Gson().fromJson(jsonStr, ApiResult::class.java)
            val tokenError = Gson().fromJson(apiResult.message, TokenError::class.java)

            if(tokenError != null){
                throw CustomOauthException(tokenError.errorMsg, tokenError.errorCode)
            }

        }catch (e: JSONException){
            logger.error("jsonEx : ${e.message}")
        }

        if (map.containsKey("active") && "true" != map["active"].toString()) {
            logger.debug("check_token returned active attribute: " + map["active"])
            throw InvalidTokenException(accessToken)
        }
        return tokenConverter.extractAuthentication(map)
    }

    override fun readAccessToken(accessToken: String?): OAuth2AccessToken {
        throw UnsupportedOperationException("Not supported: read access token")
    }

    private fun getAuthorizationHeader(clientId: String?, clientSecret: String?): String? {
        if (clientId == null || clientSecret == null) {
            logger.warn("Null Client ID or Client Secret detected. Endpoint that requires authentication will reject request with 401 error.")
        }
        val credential = String.format("%s:%s", clientId, clientSecret)
        return try {
            "Basic " + String(Base64.encode(credential.toByteArray(charset("UTF-8"))))
        } catch (e: UnsupportedEncodingException) {
            throw IllegalStateException("Could not convert String")
        }
    }

    private fun postForMap(
        path: String,
        formData: MultiValueMap<String, String>,
        headers: HttpHeaders
    ): Map<String, Any>? {
        if (headers.contentType == null) {
            headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        }
        return restTemplate.exchange<Map<String, Any>>(
            path, HttpMethod.POST,
            HttpEntity(
                formData,
                headers
            ),
            MutableMap::class.java
        ).body
    }
}