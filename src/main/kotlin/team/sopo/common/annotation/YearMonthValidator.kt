package team.sopo.common.annotation

import team.sopo.common.consts.CompletedParcelConst
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class YearMonthValidator: ConstraintValidator<DateFormatYearMonth, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return try{
            if (value== null){
                throw NullPointerException("value is null")
            }
            YearMonth.parse(value, DateTimeFormatter.ofPattern(CompletedParcelConst.yearMonthDateTimeFormatPattern))
            true
        }
        catch (e: Exception){
//            context.disableDefaultConstraintViolation()
//            context
//                .buildConstraintViolationWithTemplate("$value, please check date format")
//                .addConstraintViolation()
            false
        }
    }
}