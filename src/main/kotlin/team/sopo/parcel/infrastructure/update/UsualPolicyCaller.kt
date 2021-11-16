package team.sopo.parcel.infrastructure.update

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.ParcelStore
import team.sopo.parcel.domain.update.UpdateResult
import team.sopo.parcel.domain.update.policy.UpdatePolicyCaller

@Order(1)
@Component
class UsualPolicyCaller(private val parcelStore: ParcelStore) : UpdatePolicyCaller {
    override fun support(request: ParcelCommand.UpdateRequest): Boolean {
        return request.originalParcel.inquiryHash != request.refreshedParcel.inquiryHash
    }

    override fun update(request: ParcelCommand.UpdateRequest): UpdateResult {
        return try {
            val initParcel = request.originalParcel.apply { updateParcel(request.refreshedParcel) }
            parcelStore.store(initParcel)
            UpdateResult.SUCCESS_TO_UPDATE
        } catch (e: Exception) {
            UpdateResult.FAIL_TO_UPDATE
        }
    }
}