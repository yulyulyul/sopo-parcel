package team.sopo.parcel.domain.update.policy

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.ParcelStore
import team.sopo.parcel.domain.update.UpdateResult

@Order(3)
@Component
class OutOrphanedPolicyCaller(private val parcelStore: ParcelStore): UpdatePolicyCaller {
    override fun support(request: ParcelCommand.UpdateRequest): Boolean {
        return request.originalParcel.deliveryStatus == Parcel.DeliveryStatus.ORPHANED
    }

    override fun update(request: ParcelCommand.UpdateRequest): UpdateResult {
        return try{
            request.originalParcel.updateParcel(request.refreshedParcel)
            parcelStore.store(request.originalParcel)

            UpdateResult.SUCCESS_TO_UPDATE
        }
        catch (e: Exception){
            UpdateResult.FAIL_TO_UPDATE
        }
    }
}