package team.sopo.common.config.oauth2

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.AccessTokenConverter
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import team.sopo.common.exception.SopoOauthException
import team.sopo.common.exception.error.SopoError

class SopoResourceServerTokenService(private val tokenClient: TokenClient): ResourceServerTokenServices{
    private val logger: Logger = LogManager.getLogger(SopoResourceServerTokenService::class)

    private var tokenConverter: AccessTokenConverter = DefaultAccessTokenConverter()

    override fun loadAuthentication(accessToken: String): OAuth2Authentication {

        logger.info("accessToken : $accessToken")
        val map = tokenClient.checkToken(request = CheckTokenRequest(token = accessToken))
        logger.info("map : $map")
        if (map.containsKey("active") && "true" != map["active"].toString()) {
            logger.debug("check_token returned active attribute: " + map["active"])

            throw SopoOauthException("${accessToken}는 유효하지 않은 토큰입니다.", "", SopoError.OAUTH2_INVALID_TOKEN.code)
        }
        return tokenConverter.extractAuthentication(map)
    }

    override fun readAccessToken(accessToken: String?): OAuth2AccessToken {
        throw UnsupportedOperationException("Not supported: read access token")
    }
}