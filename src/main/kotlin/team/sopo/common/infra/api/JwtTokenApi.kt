package team.sopo.common.infra.api

import team.sopo.common.model.api.ApiResult
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

interface JwtTokenApi {
    @GET("sopo-auth/.well-known/jwks.json")
    fun getJWKSet(): Call<ApiResult<String>>

    @DELETE("sopo-auth/system/oauth/token")
    fun revokeAccessToken(
        @Query("email") email : String,
        @Query("clientId") clientId: String
    ): Call<ApiResult<String?>>

    @GET("sopo-auth/token/jwt/password-reset-token")
    fun getPasswordResetToken(
        @Query("subject") subject : String,
        @Query("expireTime") expireTime : Int,
        @Query("user") user : String,
        @Query("authCode") authCode : String
    ): Call<ApiResult<String>>
}