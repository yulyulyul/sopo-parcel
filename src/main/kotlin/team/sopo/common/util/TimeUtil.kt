package team.sopo.common.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object TimeUtil {
    fun getLocalDateTime(): LocalDateTime {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    }

    fun getLocalDate(): LocalDate {
        return LocalDate.now(ZoneId.of("Asia/Seoul"))
    }
}