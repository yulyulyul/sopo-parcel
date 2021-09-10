package team.sopo.common.infra.api

import team.sopo.common.model.api.ApiResult
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Query

interface TokenApi {

    @DELETE("sopo-auth/system/oauth/token")
    fun revokeAccessToken(
        @Query("email") email : String,
        @Query("clientId") clientId: String
    ): Call<ApiResult<String?>>
}