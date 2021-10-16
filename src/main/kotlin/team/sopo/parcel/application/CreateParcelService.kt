package team.sopo.parcel.application

import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import org.springframework.stereotype.Service

@Service
class CreateParcelService {
    fun createParcel(userId: String,  waybillNum: String, carrier: String, alias: String, trackingInfo: TrackingInfo?): Parcel {
        return Parcel(trackingInfo, userId, waybillNum, carrier, alias)
    }
}