package team.sopo.parcel.application

import team.sopo.parcel.domain.ParcelRepository
import team.sopo.parcel.domain.command.RegisterParcelCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import team.sopo.common.enums.ResponseEnum
import team.sopo.common.exception.APIException

@Service
class RegisterParcelService(
    @Autowired private val parcelRepository: ParcelRepository,
    @Autowired private val checkIsParcelRegistrableService: CheckIsParcelRegistrableService
) {

    fun registerParcel(command: RegisterParcelCommand): Long {
        checkIsParcelRegistrableService.checkRegistrable(command.userId, command.waybillNum, command.carrier.CODE)

        val parcelFromRemote = parcelRepository.getParcelFromRemote(command.carrier.CODE, command.waybillNum, command.userId, command.alias)
        val registeredParcel = parcelRepository.save(parcelFromRemote)

        return registeredParcel.id!!
    }
}