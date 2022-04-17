package team.sopo.domain.parcel.register.validator

import org.springframework.stereotype.Component
import team.sopo.common.exception.OverRegisteredParcelException
import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.ParcelReader

@Component
class RegisterLimitValidator(private val parcelReader: ParcelReader) : RegisterValidator {
    override fun validate(request: ParcelCommand.RegisterRequest) {
        val count = parcelReader.getCurrentMonthRegisteredCount(request.userToken)
        if (count >= 50) {
            throw OverRegisteredParcelException()
        }
    }
}