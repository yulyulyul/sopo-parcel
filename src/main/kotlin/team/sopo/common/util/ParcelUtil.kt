package team.sopo.common.util

import com.google.gson.Gson
import team.sopo.common.config.deliverytracker.DeliveryTrackConfig
import team.sopo.common.extension.asHex
import team.sopo.parcel.domain.DeliveryStatus
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.security.MessageDigest

@Component
class ParcelUtil {

    @Autowired
    lateinit var deliveryTrackConfig: DeliveryTrackConfig

    fun getInquiryHashFromTrackInfo(trackingInfo: TrackingInfo): String {
        val trackInfoJson = getInquiryHashResultFromTrackInfo(trackingInfo)
        return MessageDigest
                .getInstance(deliveryTrackConfig.hashAlg)
                .let {
                    it.update(trackInfoJson.toByteArray())
                    it.digest().asHex
                }
    }

    fun getInquiryHashResultFromTrackInfo(trackingInfo: TrackingInfo): String{
        return Gson().toJson(trackingInfo)
    }

    fun determineDeliveryStatus(trackingInfo: TrackingInfo?): DeliveryStatus {
        return if(trackingInfo == null){
            DeliveryStatus.NOT_REGISTERED
        }
        else{
            DeliveryStatus.valueOf(trackingInfo.state.id)
        }
    }

}