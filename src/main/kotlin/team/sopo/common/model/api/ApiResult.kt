package team.sopo.common.model.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.Gson
import team.sopo.common.enums.ResponseEnum
import team.sopo.common.extension.GetCurrentDateTime
import team.sopo.common.extension.toString
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "API 처리 결과 모델")
class ApiResult<T>(
     @Schema(name = "API 처리 완료 시간", required = true, readOnly = true, example = "2020-01-01 01:01:01")
     @JsonProperty("timestamp") var timestamp: String = GetCurrentDateTime().toString("yyyy-MM-dd HH:mm:ss"),

     @Schema(name = "API 결과 고유번호 ", required = true, readOnly = true, example = "39ccfea5-e7e4-479a-b773-31de0d754bd8")
     @JsonProperty("uniqueCode") var uniqueCode: String = "",

     @Schema(name = "처리 성공시 '0000', 이외는 모두 오류 (정확한 내용은 message 확인)", required = true, readOnly = true, example = "0000")
     @JsonProperty("code") var code: String = "",

     @Schema(name = "오류시 오류 내용이 담겨져 있음.", required = true, readOnly = true, example = "SUCCEESS")
     @JsonProperty("message") var message: String = "",

     @Schema(name = "API 호출 경로", required = true, readOnly = true, example = "/api/v1/~")
     @JsonProperty("path") var path: String = "",

     @Schema(name = "API 처리 결과 데이터", required = false, readOnly = true)
     @JsonProperty("data") var data: T? = null
){
     constructor(response: ResponseEnum):this(){
          this.code = response.CODE
          this.message = response.MSG
     }

     override fun toString(): String {
          return Gson().toJson(this)
     }
}