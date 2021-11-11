package team.sopo.parcel.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelRepository
import team.sopo.parcel.domain.update.UpdateResult
import team.sopo.push.FirebasePushService

@Service
class RefreshParcelService(
    private val parcelRepository: ParcelRepository,
    private val pushService: FirebasePushService
) {
//    private val logger: Logger = LoggerFactory.getLogger(RefreshParcelService::class.java)

    @Transactional
    fun entireRefresh(userId: String){
        val ongoingParcels = parcelRepository.getOngoingParcels(userId) ?: return

        ongoingParcels
            .filter { parcel -> parcel.deliveryStatus != Parcel.DeliveryStatus.ORPHANED }
            .forEach { parcel ->
                        if(singleRefresh(userId, parcel.id) == UpdateResult.SUCCESS_TO_UPDATE){
                            pushService.addToPushList(parcel)
                        }
            }
        pushService.sendPushMsg(userId)
    }

    @Transactional
    fun singleRefresh(userId: String, parcelId: Long): UpdateResult {
        val parcelFromDB = parcelRepository.getParcel(userId, parcelId)
        val parcelFromRemote = parcelRepository.getParcelFromRemote(parcelFromDB.carrier, parcelFromDB.waybillNum, parcelFromDB.userId, parcelFromDB.alias)

        val updatePolicy = parcelFromDB.getUpdatePolicy(parcelRepository, parcelFromRemote)
        return updatePolicy.run()
    }
}