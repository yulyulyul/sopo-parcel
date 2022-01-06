package team.sopo.common.annotation

import java.util.*
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import kotlin.Enum
import kotlin.reflect.KClass


class EnumValidator: ConstraintValidator<team.sopo.common.annotation.Enum, String> {

    private var annotation: team.sopo.common.annotation.Enum? = null

    private fun KClass<out Enum<*>>.enumConstantNames() = this.java.enumConstants.map(Enum<*>::name)

    override fun initialize(constraintAnnotation: team.sopo.common.annotation.Enum?) {
        this.annotation = constraintAnnotation
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        value ?: return false
        val enums = this.annotation?.enumClass?.enumConstantNames() ?: return false

        return enums.contains(value)
    }
}