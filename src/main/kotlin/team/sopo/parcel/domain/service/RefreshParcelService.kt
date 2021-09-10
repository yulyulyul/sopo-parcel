package team.sopo.parcel.domain.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelRepository
import team.sopo.parcel.domain.update.UpdateResult
import team.sopo.push.FirebasePushService

@Service
class RefreshParcelService(
    @Autowired private val parcelRepository: ParcelRepository,
    @Autowired private val pushService: FirebasePushService
) {

    fun entireRefresh(userId: String){
        val ongoingParcels = parcelRepository.getOngoingParcels(userId) ?: return

        ongoingParcels.forEach { parcel ->
            if(singleRefresh(userId, parcel) == UpdateResult.SUCCESS_TO_UPDATE)
                pushService.addToPushList(parcel)
        }
        pushService.sendPushMsg(userId)
    }

    fun singleRefresh(userId: String, parcelId: Long): UpdateResult {
        val refreshedParcel: Parcel = parcelRepository.getRefreshedParcel(userId, parcelId)
        val parcel = parcelRepository.getParcel(userId, parcelId)

        val updatePolicy = parcel.getUpdatePolicy(parcelRepository, refreshedParcel)
        return updatePolicy.run()
    }

    fun singleRefresh(userId: String, parcel: Parcel): UpdateResult {
        val refreshedParcel: Parcel = parcelRepository.getRefreshedParcel(userId, parcel.id ?: throw NullPointerException("parcel id is null"))
        val updatePolicy = parcel.getUpdatePolicy(parcelRepository, refreshedParcel)
        return updatePolicy.run()
    }

}