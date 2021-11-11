package team.sopo.parcel

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.ZonedDateTime

class SopoParcelApplicationTests {

    @Test
    fun test() {
        val time = "2021-08-25T10:35:00"
        val parse = ZonedDateTime.parse(time.plus("+09:00[Asia/Seoul]"))
        println("parse : $parse")
    }

}
