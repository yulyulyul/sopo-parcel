package team.sopo.parcel.infrastructure.update

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.update.ProcessResult
import team.sopo.parcel.domain.update.UpdateStatus
import team.sopo.parcel.domain.update.policy.UpdatePolicyCaller

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