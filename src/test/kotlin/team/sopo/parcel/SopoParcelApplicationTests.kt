package team.sopo.parcel

import org.junit.jupiter.api.Test
import team.sopo.domain.parcel.carrier.Carrier
import team.sopo.domain.parcel.Parcel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class SopoParcelApplicationTests {

    @Test
    fun test() {
        val time = "2021-08-25T10:35:00"
        val parse = ZonedDateTime.parse(time.plus("+09:00[Asia/Seoul]"))
        println("parse : $parse")
    }

    @Test
    fun dateTest(){
        val date = Date(1646292849000)
        println(date)
    }
}
