package team.sopo.parcel.domain.register.validator

import org.springframework.stereotype.Component
import team.sopo.common.exception.OverRegisteredParcelException
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.ParcelReader

@Component
class RegisterLimitValidator(private val parcelReader: ParcelReader) : RegisterValidator {
    override fun validate(request: ParcelCommand.RegisterRequest) {
        val count = parcelReader.getCurrentMonthRegisteredCount(request.userId)
        if (count > 50) {
            throw OverRegisteredParcelException()
        }
    }
}