package team.sopo.parcel.infrastructure.update

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.ParcelStore
import team.sopo.parcel.domain.update.ProcessResult
import team.sopo.parcel.domain.update.UpdateStatus
import team.sopo.parcel.domain.update.policy.UpdatePolicyCaller

@Order(3)
@Component
class OutOrphanedPolicyCaller(private val parcelStore: ParcelStore) : UpdatePolicyCaller {
    override fun support(request: ParcelCommand.UpdateRequest): Boolean {
        return request.originalParcel.deliveryStatus == Parcel.DeliveryStatus.ORPHANED
    }

    override fun update(request: ParcelCommand.UpdateRequest): ProcessResult {
        return try {
            val initParcel = request.originalParcel.apply { updateParcel(request.refreshedParcel) }
            val updatedParcel = parcelStore.store(initParcel)
            val updateStatus = UpdateStatus.SUCCESS_TO_UPDATE

            ProcessResult(updatedParcel, updateStatus)
        } catch (e: Exception) {
            ProcessResult(request.originalParcel, UpdateStatus.FAIL_TO_UPDATE)
        }
    }
}