package team.sopo.parcel.application

import team.sopo.parcel.domain.ParcelRepository
import team.sopo.parcel.domain.command.UpdateAliasCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChangeParcelService(
    @Autowired private val parcelRepository: ParcelRepository
) {

    fun changeAlias(command: UpdateAliasCommand){
        val parcel = parcelRepository.getParcel(command.userId, command.parcelId)
        parcel.changeParcelAlias(command.alias)
        parcelRepository.save(parcel)
    }

    @Transactional
    fun deleteParcels(userId: String, deleteList: List<Long>){
        for(parcelId in deleteList){
            val parcel = parcelRepository.getParcel(userId, parcelId)
            parcel.inactivate()
            parcelRepository.save(parcel)
        }
    }

}