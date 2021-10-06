package team.sopo.parcel.domain.update

import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelRepository

class UsualUpdate(val parcelRepository: ParcelRepository, private val targetParcel: Parcel, private val newParcel: Parcel): UpdatePolicy() {

    override fun run(): UpdateResult {
        return try{
            targetParcel.updateParcel(newParcel)
            parcelRepository.save(targetParcel)
            UpdateResult.SUCCESS_TO_UPDATE
        }
        catch (e: Exception){
            UpdateResult.FAIL_TO_UPDATE
        }
    }
}