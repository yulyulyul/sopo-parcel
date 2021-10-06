package team.sopo.parcel.domain.update

import team.sopo.parcel.domain.DeliveryStatus
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelRepository

class ChangeStatusToUnidentifiedDeliveredParcel(val parcelRepository: ParcelRepository, private val parcel: Parcel): UpdatePolicy() {

    override fun run(): UpdateResult {
        parcel.changeDeliveryStatus(DeliveryStatus.ORPHANED)
        parcelRepository.save(parcel)
        return UpdateResult.NEED_TO_CHECK_DELIVERY_STATUS
    }
}