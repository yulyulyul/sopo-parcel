package team.sopo.common.annotation

import javax.validation.Constraint
import kotlin.Enum
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EnumValidator::class])
annotation class Enum(
    val message: String = "정의되지 않은 Enum",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Any>> = [],
    val enumClass: KClass<out Enum<*>>,
    val ignoreCase: Boolean = false
)
