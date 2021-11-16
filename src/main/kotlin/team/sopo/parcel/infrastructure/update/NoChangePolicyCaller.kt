package team.sopo.parcel.infrastructure.update

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.update.UpdateResult
import team.sopo.parcel.domain.update.policy.UpdatePolicyCaller

@Order(4)
@Component
class NoChangePolicyCaller : UpdatePolicyCaller {
    override fun support(request: ParcelCommand.UpdateRequest): Boolean {
        return request.originalParcel.inquiryHash == request.refreshedParcel.inquiryHash
    }

    override fun update(request: ParcelCommand.UpdateRequest): UpdateResult {
        return UpdateResult.NO_CHANGE
    }
}