package team.sopo.infrastructure.parcel.tracker.sweet

import com.fasterxml.jackson.annotation.JsonProperty

data class TrackingDetail(
    @JsonProperty("code") val code: String?,            // 배송상태 코드
    @JsonProperty("kind") val kind: String,             // 진행상태
    @JsonProperty("level") val level: Int,              // 진행단계
    @JsonProperty("manName") val manName: String,       // 배송기사 이름
    @JsonProperty("manPic") val manPic: String,         // 배송기사 전화번호
    @JsonProperty("remark") val remark: String?,        // 비고
    @JsonProperty("telno") val telno: String,           // 진행위치(지점) 전화번호
    @JsonProperty("telno2") val telno2: String,         // 배송기사 전화번호
    @JsonProperty("time") val time: Long,               // 진행시간
    @JsonProperty("timeString") val timeString: String, // 진행시간
    @JsonProperty("where") val where: String            // 진행위치지점
)