package team.sopo.infrastructure.push

import org.springframework.stereotype.Service
import team.sopo.domain.push.PushInfo
import team.sopo.domain.push.PushService

@Service
class PushServiceImpl(
    private val pushClient: PushClient
) : PushService {
    override fun pushToUpdateParcel(userToken: String, parcelIds: List<Long>) {
        pushClient.pushToUpdateParcel(PushInfo.UpdateParcel(userToken, parcelIds))
    }

    override fun pushToAwakenDevice(topic: String) {
        pushClient.pushToAwakeDevice(PushInfo.AwakeDevice(topic))
    }
}