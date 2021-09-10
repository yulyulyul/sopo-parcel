package team.sopo.parcel.infrastructure.datasource.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import team.sopo.parcel.application.CreateParcelService
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import team.sopo.parcel.infrastructure.FeignParcelInquiryService
import team.sopo.parcel.infrastructure.datasource.ParcelRemoteDataSource

@Component
class FeignParcelRemoteDataSource(
    @Autowired private val inquirySvc: FeignParcelInquiryService,
    @Autowired private val createParcelService: CreateParcelService
): ParcelRemoteDataSource {

    override fun getRefreshedParcel(oldParcel: Parcel, userId: String): Parcel {
        val trackingInfo: TrackingInfo = inquirySvc.inquiryParcel(oldParcel.carrier, oldParcel.waybillNum) ?: return oldParcel
        return oldParcel.updateParcel(trackingInfo)
    }

    override fun getParcelFromRemote(carrier: String, waybillNum: String, userId: String, alias: String): Parcel {
        val trackingInfo: TrackingInfo? = inquirySvc.inquiryParcel(carrier, waybillNum)
        return createParcelService.createParcel(userId, waybillNum, carrier, alias, trackingInfo)
    }
}