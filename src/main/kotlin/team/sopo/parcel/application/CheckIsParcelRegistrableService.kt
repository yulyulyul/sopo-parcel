package team.sopo.parcel.application

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import team.sopo.common.enums.ResponseEnum
import team.sopo.common.exception.APIException
import team.sopo.parcel.domain.ParcelRepository

@Service
class CheckIsParcelRegistrableService(
    @Autowired private val parcelRepository: ParcelRepository
) {

    fun checkRegistrable(userId: String, waybillNum: String, carrier: String){
        val isRegistered = parcelRepository.isAlreadyRegistered(userId,  waybillNum, carrier)

        if(isRegistered){
            throw APIException(ResponseEnum.ALREADY_REGISTERED_PARCEL)
        }

        val isLimitOver = parcelRepository.isLimitCountOver(userId)
        if(isLimitOver){
            throw APIException(ResponseEnum.OVER_REGISTERED_PARCEL)
        }
    }
}