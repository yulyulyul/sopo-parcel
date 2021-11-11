package team.sopo.parcel.application

import org.springframework.stereotype.Service
import team.sopo.parcel.domain.ParcelRepository
import team.sopo.parcel.domain.command.RegisterParcelCommand

@Service
class RegisterParcelService(
    private val parcelRepository: ParcelRepository,
    private val checkIsParcelRegistrableService: CheckIsParcelRegistrableService
) {

    fun registerParcel(command: RegisterParcelCommand): Long {
        checkIsParcelRegistrableService.checkRegistrable(command.userId, command.waybillNum, command.carrier.CODE)

        val parcelFromRemote = parcelRepository.getParcelFromRemote(command.carrier.CODE, command.waybillNum, command.userId, command.alias)
        val registeredParcel = parcelRepository.save(parcelFromRemote)

        return registeredParcel.id
    }
}