package team.sopo.parcel.infrastructure.update

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.ParcelStore
import team.sopo.parcel.domain.update.UpdateResult
import team.sopo.parcel.domain.update.policy.UpdatePolicyCaller
import java.time.ZonedDateTime

@Order(2)
@Component
class ChangeToOrphanedPolicyCaller(private val parcelStore: ParcelStore): UpdatePolicyCaller {
    override fun support(request: ParcelCommand.UpdateRequest): Boolean {
        return (request.originalParcel.deliveryStatus == Parcel.DeliveryStatus.NOT_REGISTERED) && (request.originalParcel.auditDte!!.plusWeeks(2L).isBefore(ZonedDateTime.now()))
    }

    override fun update(request: ParcelCommand.UpdateRequest): UpdateResult {
        return try{
            val initParcel = request.originalParcel.apply { changeToOrphaned() }
            parcelStore.store(initParcel)
            UpdateResult.SUCCESS_TO_UPDATE
        }
        catch (e: Exception){
            UpdateResult.FAIL_TO_UPDATE
        }
    }
}