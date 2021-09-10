package team.sopo.parcel.infrastructure

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import team.sopo.parcel.application.InquiryService
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

@Service
class FeignParcelInquiryService(
    @Autowired private val parcelInquiryService: DeliveryClient
): InquiryService {
    override fun inquiryParcel(carrier: String, waybillNum: String): TrackingInfo? {
        return parcelInquiryService.getTrackingInfo(carrier, waybillNum)
    }
}