package team.sopo.common.infra.impl

import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.JWTParser
import team.sopo.common.config.retrofit.RetrofitConfiguration
import team.sopo.common.enums.ResponseEnum
import team.sopo.common.exception.APIException
import team.sopo.common.infra.JwtTokenService
import team.sopo.common.infra.api.JwtTokenApi
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Component
import java.text.ParseException
import java.util.*

@Component
class NimbusdsJwtTokenService(private val networkManager: RetrofitConfiguration): JwtTokenService {

    private val logger: Logger = LogManager.getLogger(this.javaClass.name)

    override fun getClaimSet(token:  String): JWTClaimsSet {

        val jwt = try{
             JWTParser.parse(token)
        }
        catch (e : ParseException){
            logger.error("error => getClaimSet : ${e.message}")
            throw APIException(ResponseEnum.VALIDATION_ERROR, "Fail to parse jwt token.")
        }

        if(isValid(jwt)){
            return jwt.jwtClaimsSet
        }
        else{
            throw APIException(ResponseEnum.VALIDATION_ERROR, "Jwt token is not valid.")
        }
    }

    override fun getData(claimSet: JWTClaimsSet, key: String): Any {
        val claims = claimSet.claims
        if (claims.containsKey(key)){
            val data = claimSet.claims[key]
            logger.debug("getData => data : $data")
            return data ?: throw APIException(ResponseEnum.VALIDATION_ERROR, "Fail to get data(key = $key), data is null")
        }
        else{
            logger.error("getData => It does not contain that key($key).")
            throw APIException(ResponseEnum.VALIDATION_ERROR, "It does not contain that key($key).")
        }
    }

    private fun isValid(jwt: JWT): Boolean {
        val jwsObj = getJWSObj(jwt)
        logger.debug("isValid , jwsObj : ${jwsObj.serialize()}")
        val jwkSet = getJWKSet()
        logger.debug("isValid , jwsObj : ${jwkSet.toJSONObject().toJSONString()}")
        val keyId = getKeyId(jwt)
        logger.debug("isValid , keyId : $keyId")
        val jwk = jwkSet.getKeyByKeyId(keyId) ?: throw APIException(ResponseEnum.VALIDATION_ERROR, "Fail to get jwk from keyId($keyId)")
        logger.debug("isValid , jwk : ${jwk.toJSONObject().toJSONString()}")
        val rsaKey: RSAKey = jwk.toRSAKey() ?: throw APIException(ResponseEnum.VALIDATION_ERROR, "Fail to transform jwk to rsa-key")
        logger.debug("isValid , rsaKey : $rsaKey")
        val verifier = RSASSAVerifier(rsaKey)
        logger.debug("isValid , verifier : $verifier")

        val now = Date()
        val expirationTime = getTokenExpirationTime(jwt)
        logger.debug("@@ -> isValid , expirationTime : $expirationTime")

        return jwsObj.verify(verifier) && now.before(expirationTime)
    }

    private fun getTokenExpirationTime(jwt: JWT): Date {
        return jwt.jwtClaimsSet.expirationTime ?: throw APIException(ResponseEnum.VALIDATION_ERROR, "Jwt token is expired")
    }

    private fun getJWSObj(jwt: JWT): JWSObject{
       return JWSObject.parse(jwt.serialize()) ?: throw APIException(ResponseEnum.VALIDATION_ERROR, "Fail to parse jws")
    }

    private fun getKeyId(jwt: JWT): String{
        try{
            val jwtHeaderJsonObj = jwt.header.toJSONObject()
            if(jwtHeaderJsonObj.containsKey("kid")){
                return jwt.header.toJSONObject()["kid"].toString()
            }
            else{
                throw APIException(ResponseEnum.VALIDATION_ERROR, "It does not contain valid key-id")
            }
        }
        catch (e: Exception){
            throw APIException(ResponseEnum.VALIDATION_ERROR, "Fail to get key-id.")
        }
    }

    private fun getJWKSet(): JWKSet{
        val apiResult = networkManager
                            .getSopoApi()
                            .create(JwtTokenApi::class.java)
                            .getJWKSet()
                            .execute()

        logger.debug("JwkSet Result : $apiResult")
        return  if(apiResult.isSuccessful){
            val jwkSetString = apiResult.body()?.data ?: throw APIException(ResponseEnum.VALIDATION_ERROR, "Fail to get jwk-set data")
            JWKSet.parse(jwkSetString)
        }
        else{
            throw APIException(ResponseEnum.VALIDATION_ERROR, "Fail to parse jwk-set")
        }
    }
}