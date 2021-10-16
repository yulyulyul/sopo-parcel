package team.sopo.parcel.domain.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelRepository
import team.sopo.parcel.domain.update.UpdateResult
import team.sopo.push.FirebasePushService

@Service
class RefreshParcelService(
    @Autowired private val parcelRepository: ParcelRepository,
    @Autowired private val pushService: FirebasePushService
) {
    private val logger: Logger = LoggerFactory.getLogger(RefreshParcelService::class.java)

    @Transactional
    fun entireRefresh(userId: String){
        val ongoingParcels = parcelRepository.getOngoingParcels(userId) ?: return

        ongoingParcels.forEach { parcel ->
            if(singleRefresh(userId, parcel) == UpdateResult.SUCCESS_TO_UPDATE)
                pushService.addToPushList(parcel)
        }
        pushService.sendPushMsg(userId)

        logger.info("@@ success @@")
    }

    @Transactional
    fun singleRefresh(userId: String, parcelId: Long): UpdateResult {
        val refreshedParcel: Parcel = parcelRepository.getRefreshedParcel(userId, parcelId)
        val parcel = parcelRepository.getParcel(userId, parcelId)

        val updatePolicy = parcel.getUpdatePolicy(parcelRepository, refreshedParcel)
        return updatePolicy.run()
    }

    @Transactional
    fun singleRefresh(userId: String, parcel: Parcel): UpdateResult {
        val parcelFromRemote = parcelRepository.getParcelFromRemote(parcel.carrier, parcel.waybillNum, parcel.userId, parcel.alias)
//        val refreshedParcel: Parcel = parc elRepository.getRefreshedParcel(userId, parcel.id ?: throw ParcelNotFoundException())
        val updatePolicy = parcel.getUpdatePolicy(parcelRepository, parcelFromRemote)
        return updatePolicy.run()
    }

}