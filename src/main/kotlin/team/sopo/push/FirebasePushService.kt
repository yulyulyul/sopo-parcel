package team.sopo.push

import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.update.UpdatedParcelInfo
@Service
class FirebasePushService(
    @Autowired private val pushList: MutableList<UpdatedParcelInfo>,
    @Autowired private val userClient: UserClient
): PushService(pushList) {

    private val logger: Logger = LoggerFactory.getLogger(FirebasePushService::class.java)

    override fun sendPushMsg(userId: String){

        if(pushList.isEmpty()){
            return
        }

        val config = AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build() ?: throw Exception("AndroidConfig is null")
        logger.info("Push List : $pushList")

        val message: Message = Message.builder()
                .putData("notificationId", NotificationType.PUSH_UPDATE_PARCEL.code)
                .putData("data", Gson().toJson(pushList))
                .setToken(getClientFcmToken(userId))
                .setAndroidConfig(config)
                .build()

        val response = FirebaseMessaging.getInstance().send(message)
        logger.info("Successfully sent message: $response")
    }

    override fun addToPushList(parcel: Parcel){
        val parcelId = parcel.id ?: throw NullPointerException("parcel id is null")
        val deliveryStatus = parcel.deliveryStatus ?: throw NullPointerException("deliveryStatus id is null")

        pushList.add(UpdatedParcelInfo(parcelId, deliveryStatus.name))
    }

    private fun getClientFcmToken(userId: String):String{
        return userClient.getFcmToken(userId).data?.fcmToken ?: throw NullPointerException("fcmToken is null")
    }
}