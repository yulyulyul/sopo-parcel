package team.sopo.parcel.domain.register.validator

import org.springframework.stereotype.Component
import team.sopo.common.exception.AlreadyRegisteredParcelException
import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.ParcelReader

@Component
class AlreadyRegisteredParcelValidator(private val parcelReader: ParcelReader): RegisterValidator {
    override fun validate(request: ParcelCommand.RegisterRequest) {
        try{
            val parcel = parcelReader.getParcel(request.userId, request.carrier, request.waybillNum)
            if(parcel.isActivate()){
                throw AlreadyRegisteredParcelException()
            }
        }
        catch (e: ParcelNotFoundException){ }
    }
}