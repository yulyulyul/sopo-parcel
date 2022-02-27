package team.sopo.infrastructure.parcel.update

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.update.ProcessResult
import team.sopo.domain.parcel.update.UpdateStatus
import team.sopo.domain.parcel.update.policy.UpdatePolicyCaller

@Order(4)
@Component
class NoChangePolicyCaller : UpdatePolicyCaller {
    override fun support(request: ParcelCommand.UpdateRequest): Boolean {
        return request.originalParcel.inquiryHash == request.refreshedParcel.inquiryHash
    }

    override fun update(request: ParcelCommand.UpdateRequest): ProcessResult {
        return ProcessResult(request.originalParcel, UpdateStatus.NO_CHANGE)
    }
}