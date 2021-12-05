package team.sopo.common.model.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.Gson
import team.sopo.common.extension.getCurrentDateTime
import team.sopo.common.extension.toString
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "API 처리 결과 모델")
class ApiResult<T>(
     @Schema(name = "API 처리 완료 시간", required = true, readOnly = true, example = "2020-01-01 01:01:01")
     @JsonProperty("timestamp") var timestamp: String = getCurrentDateTime().toString("yyyy-MM-dd HH:mm:ss"),

    @Schema(name = "응답 내용", required = true, readOnly = true, example = "SUCCESS")
     @JsonProperty("message") var message: String = "SUCCESS",

    @Schema(name = "API 호출 경로", required = true, readOnly = true, example = "/api/v1/~")
     @JsonProperty("path") var path: String = "",

    @Schema(name = "API 처리 결과 데이터", required = false, readOnly = true)
     @JsonProperty("data") var data: T? = null
){
     override fun toString(): String {
          return Gson().toJson(this)
     }
}