package team.sopo.parcel.infrastructure.update

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.ParcelStore
import team.sopo.parcel.domain.update.ProcessResult
import team.sopo.parcel.domain.update.UpdateStatus
import team.sopo.parcel.domain.update.policy.UpdatePolicyCaller
import java.time.ZonedDateTime

@Order(2)
@Component
class ChangeToOrphanedPolicyCaller(private val parcelStore: ParcelStore): UpdatePolicyCaller {
    override fun support(request: ParcelCommand.UpdateRequest): Boolean {
        return (request.originalParcel.deliveryStatus == Parcel.DeliveryStatus.NOT_REGISTERED) && (request.originalParcel.auditDte!!.plusWeeks(2L).isBefore(ZonedDateTime.now()))
    }

    override fun update(request: ParcelCommand.UpdateRequest): ProcessResult {
        return try{
            val initParcel = request.originalParcel.apply { changeToOrphaned() }
            val orphanedParcel = parcelStore.store(initParcel)

            ProcessResult(orphanedParcel, UpdateStatus.SUCCESS_TO_UPDATE)
        }
        catch (e: Exception){
            ProcessResult(request.originalParcel, UpdateStatus.FAIL_TO_UPDATE)
        }
    }
}