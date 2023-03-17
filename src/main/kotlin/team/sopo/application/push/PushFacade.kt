package team.sopo.application.push

import org.springframework.stereotype.Service
import team.sopo.domain.push.PushCommand
import team.sopo.domain.push.PushService

@Service
class PushFacade(
    private val pushService: PushService
) {
    fun pushToAwakeDevice(command: PushCommand.PushToAwakeDevice) {
        pushService.pushToAwakenDevice(command.topic)
    }

    fun pushToParcelUpdate(command: PushCommand.PushToParcelUpdate) {
        pushService.pushToUpdateParcel(command.pushToken, command.parcelIds)
    }
}