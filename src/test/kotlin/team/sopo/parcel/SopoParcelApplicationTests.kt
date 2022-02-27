package team.sopo.parcel

import org.junit.jupiter.api.Test
import team.sopo.domain.parcel.Carrier
import team.sopo.domain.parcel.Parcel
import java.time.ZoneId
import java.time.ZonedDateTime

class SopoParcelApplicationTests {

    @Test
    fun test() {
        val time = "2021-08-25T10:35:00"
        val parse = ZonedDateTime.parse(time.plus("+09:00[Asia/Seoul]"))
        println("parse : $parse")
    }

    @Test
    fun makeParcelTest(){
        val userId = 1L
        val minusWeeks = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusWeeks(3L)

        val initParcel =
            Parcel(null, userId, "test_waybillNum_1", Carrier.CJ_LOGISTICS.CODE, "test_parcel (1)").apply {
                deliveryStatus = Parcel.DeliveryStatus.NOT_REGISTERED
                regDte = minusWeeks
                auditDte = minusWeeks
            }

        println("regDte : ${initParcel.regDte}")
        println("auditDte : ${initParcel.auditDte}")
    }

}
