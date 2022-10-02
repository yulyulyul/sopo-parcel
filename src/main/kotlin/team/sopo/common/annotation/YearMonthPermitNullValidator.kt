package team.sopo.common.annotation

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class YearMonthPermitNullValidator : ConstraintValidator<CheckYYYYMMPermitNull, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return if (value == null) {
            true
        } else {
            try {
                YearMonth.parse(value, DateTimeFormatter.ofPattern("yyyyMM"))
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}