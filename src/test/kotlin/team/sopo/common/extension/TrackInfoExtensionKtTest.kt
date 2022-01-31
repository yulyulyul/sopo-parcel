package team.sopo.common.extension

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import team.sopo.parcel.domain.trackinginfo.TrackingInfo

class TrackInfoExtensionKtTest(){

    @Test
    fun sortProgressTest(){
        // Given
        val inquiryResult = "{\"from\":{\"name\":\"부산장전(대)\",\"time\":\"2021-06-02T12:32:00+09:00\"},\"to\":{\"name\":\"여의도(대)\"},\"state\":{\"id\":\"at_pickup\",\"text\":\"상품인수\"},\"progresses\":[{\"time\":\"2021-06-02T12:32:00+09:00\",\"location\":{\"name\":\"\\n                                \\t여의도(대)\\n                                \"},\"status\":{\"id\":\"delivered\",\"text\":\"배송완료\"},\"description\":\"배달 완료하였습니다.(배송담당: 박영택 010-2898-3924)\"},{\"time\":\"2021-06-02T09:14:00+09:00\",\"location\":{\"name\":\"\\n                                \\t여의도(대)\\n                                \"},\"status\":{\"id\":\"out_for_delivery\",\"text\":\"배송출발\"},\"description\":\"고객님의 상품을  여의도(대)에 배달 예정 입니다.(배송담당: 김은혜 010-5338-9638)\"},{\"time\":\"2021-06-02T08:30:00+09:00\",\"location\":{\"name\":\"\\n                                \\t광명집배센터(여의도)\\n                                \"},\"status\":{\"id\":\"in_transit\",\"text\":\"이동중\"},\"description\":\"광명집배센터(여의도)에 도착하였습니다.\"},{\"time\":\"2021-06-02T06:41:00+09:00\",\"location\":{\"name\":\"\\n                                \\t서울구로TML\\n                                \"},\"status\":{\"id\":\"in_transit\",\"text\":\"이동중\"},\"description\":\"서울구로TML에 도착하였습니다.\"},{\"time\":\"2021-06-01T22:11:00+09:00\",\"location\":{\"name\":\"\\n                                \\t부산서부TML\\n                                \"},\"status\":{\"id\":\"in_transit\",\"text\":\"이동중\"},\"description\":\"부산서부TML에 도착하였습니다.\"},{\"time\":\"2021-06-01T21:00:00+09:00\",\"location\":{\"name\":\"\\n                                \\t부산장전(대)\\n                                \"},\"status\":{\"id\":\"at_pickup\",\"text\":\"상품인수\"},\"description\":\"보내시는 고객님으로부터 상품을 인수했습니다.\"}],\"carrier\":{\"id\":\"kr.lotte\",\"name\":\"롯데택배\",\"tel\":\"+8215882121\"}}"
        val trackingInfo = Gson().fromJson(inquiryResult, TrackingInfo::class.java).removeSpecialCharacter()

        // When
        trackingInfo.sortProgress()

        // Then
        Assertions.assertEquals("at_pickup", trackingInfo.progresses.first()!!.status.id)
        Assertions.assertEquals("delivered", trackingInfo.progresses.last()!!.status.id)
    }

    @Test
    fun verifyStateTest(){
        // Given
        val inquiryResult = "{\"from\":{\"name\":\"부산장전(대)\",\"time\":\"2021-06-02T12:32:00+09:00\"},\"to\":{\"name\":\"여의도(대)\"},\"state\":{\"id\":\"at_pickup\",\"text\":\"상품인수\"},\"progresses\":[{\"time\":\"2021-06-02T12:32:00+09:00\",\"location\":{\"name\":\"\\n                                \\t여의도(대)\\n                                \"},\"status\":{\"id\":\"delivered\",\"text\":\"배송완료\"},\"description\":\"배달 완료하였습니다.(배송담당: 박영택 010-2898-3924)\"},{\"time\":\"2021-06-02T09:14:00+09:00\",\"location\":{\"name\":\"\\n                                \\t여의도(대)\\n                                \"},\"status\":{\"id\":\"out_for_delivery\",\"text\":\"배송출발\"},\"description\":\"고객님의 상품을  여의도(대)에 배달 예정 입니다.(배송담당: 김은혜 010-5338-9638)\"},{\"time\":\"2021-06-02T08:30:00+09:00\",\"location\":{\"name\":\"\\n                                \\t광명집배센터(여의도)\\n                                \"},\"status\":{\"id\":\"in_transit\",\"text\":\"이동중\"},\"description\":\"광명집배센터(여의도)에 도착하였습니다.\"},{\"time\":\"2021-06-02T06:41:00+09:00\",\"location\":{\"name\":\"\\n                                \\t서울구로TML\\n                                \"},\"status\":{\"id\":\"in_transit\",\"text\":\"이동중\"},\"description\":\"서울구로TML에 도착하였습니다.\"},{\"time\":\"2021-06-01T22:11:00+09:00\",\"location\":{\"name\":\"\\n                                \\t부산서부TML\\n                                \"},\"status\":{\"id\":\"in_transit\",\"text\":\"이동중\"},\"description\":\"부산서부TML에 도착하였습니다.\"},{\"time\":\"2021-06-01T21:00:00+09:00\",\"location\":{\"name\":\"\\n                                \\t부산장전(대)\\n                                \"},\"status\":{\"id\":\"at_pickup\",\"text\":\"상품인수\"},\"description\":\"보내시는 고객님으로부터 상품을 인수했습니다.\"}],\"carrier\":{\"id\":\"kr.lotte\",\"name\":\"롯데택배\",\"tel\":\"+8215882121\"}}"
        val trackingInfo = Gson().fromJson(inquiryResult, TrackingInfo::class.java).apply {
            removeSpecialCharacter()
            sortProgress()
        }

        // When
        trackingInfo.verifyState()

        // Then
        Assertions.assertEquals("delivered", trackingInfo.state.id)
    }

}