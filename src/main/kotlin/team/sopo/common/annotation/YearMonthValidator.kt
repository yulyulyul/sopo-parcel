package team.sopo.common.annotation

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class YearMonthValidator: ConstraintValidator<CheckYYYYMM, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return try{
            if (value== null){
                throw NullPointerException("value is null")
            }
            YearMonth.parse(value, DateTimeFormatter.ofPattern("yyyyMM"))
            true
        }
        catch (e: Exception){
            false
        }
    }
}