package team.sopo.push

import com.google.api.client.http.HttpResponseException
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.gson.Gson
import org.apache.kafka.common.errors.ResourceNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import team.sopo.common.exception.InsufficientConditionException
import team.sopo.common.exception.SystemException
import team.sopo.parcel.ParcelInfo
import team.sopo.push.dto.UpdatedParcelInfo

@Service
class FirebasePushService(
    private val pushList: MutableList<UpdatedParcelInfo>,
    private val userClient: UserClient
): PushService(pushList) {

    private val logger: Logger = LoggerFactory.getLogger(FirebasePushService::class.java)

    @Transactional(propagation = Propagation.SUPPORTS)
    override fun sendPushMsg(userId: String){

        if(pushList.isEmpty()){
            return
        }

        val config = AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build() ?: throw SystemException("푸쉬관련 안드로이드 설정에 실패하였습니다.")
        logger.info("Push List : $pushList")

        val message: Message = Message.builder()
                .putData("notificationId", NotificationType.PUSH_UPDATE_PARCEL.code)
                .putData("data", Gson().toJson(pushList))
                .setToken(getClientFcmToken(userId))
                .setAndroidConfig(config)
                .build()

        try{
            val response = FirebaseMessaging.getInstance().send(message)
            logger.info("Successfully sent message: $response")
        }
        catch (e: FirebaseMessagingException){
            val fcmException = e.cause as HttpResponseException

            logger.error("fcm error message => ${fcmException.message}")
            throw InsufficientConditionException(e.message ?: "push 관련해서 문제가 있습니다. 다음에 다시시도 해주세요.")

        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    override fun addToPushList(parcel: ParcelInfo.Main){
        val parcelId = parcel.parcelId ?: throw InsufficientConditionException("택배 id가 정의되지 않아 해당 요청을 수행할 수 없습니다.")
        val deliveryStatus = parcel.deliveryStatus ?: throw InsufficientConditionException("배송 상태가 등록되지 않아 해당 요청을 수행할 수 없습니다.")

        pushList.add(UpdatedParcelInfo(parcelId, deliveryStatus.name))
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    override fun addToPushList(parcelList: List<ParcelInfo.Main>){
        parcelList.stream()
            .forEach { parcel -> addToPushList(parcel) }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    fun getClientFcmToken(userId: String):String{
        return userClient.getFcmToken(userId).data?.fcmToken ?: throw ResourceNotFoundException("fcmToken을 요청했으나 데이터가 존재하지 않습니다.")
    }
}