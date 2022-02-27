package team.sopo.infrastructure.parcel.update

import org.springframework.stereotype.Component
import team.sopo.common.exception.InsufficientConditionException
import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.update.ProcessResult
import team.sopo.domain.parcel.update.policy.UpdatePolicyCaller
import team.sopo.domain.parcel.update.UpdateProcessor

@Component
class UpdateProcessorImpl(private val updaterList: List<UpdatePolicyCaller>) : UpdateProcessor {
    override fun update(request: ParcelCommand.UpdateRequest): ProcessResult {
        val updater = routingUpdatePolicyCaller(updaterList, request)
        return updater.update(request)
    }

    private fun routingUpdatePolicyCaller(
        updaterList: List<UpdatePolicyCaller>,
        request: ParcelCommand.UpdateRequest
    ): UpdatePolicyCaller {
        return updaterList.stream()
            .filter { it.support(request) }
            .findFirst()
            .orElseThrow { InsufficientConditionException("지원되는 업데이트 정책이 존재하지 않습니다.") }
    }
}