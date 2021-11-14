package team.sopo.parcel.infrastructure.parcel.update

import org.springframework.stereotype.Component
import team.sopo.common.exception.InsufficientConditionException
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.update.policy.UpdatePolicyCaller
import team.sopo.parcel.domain.update.UpdateProcessor
import team.sopo.parcel.domain.update.UpdateResult

@Component
class UpdateProcessorImpl(private val updaterList: List<UpdatePolicyCaller>): UpdateProcessor {
    override fun update(request: ParcelCommand.UpdateRequest): UpdateResult {
        val updater = routingUpdatePolicyCaller(updaterList, request)
        return updater.update(request)
    }

    private fun routingUpdatePolicyCaller(updaterList: List<UpdatePolicyCaller>, request: ParcelCommand.UpdateRequest): UpdatePolicyCaller {
        return updaterList.stream()
            .filter { it.support(request) }
            .findFirst()
            .orElseThrow { InsufficientConditionException("지원되는 업데이트 정책이 존재하지 않습니다.") }
    }
}