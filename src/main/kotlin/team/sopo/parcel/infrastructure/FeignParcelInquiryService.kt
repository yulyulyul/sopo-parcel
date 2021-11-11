package team.sopo.parcel.infrastructure

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import team.sopo.common.exception.FailToSearchParcelException
import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.parcel.application.InquiryService
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

@Service
class FeignParcelInquiryService(
    @Autowired private val deliveryClient: DeliveryClient
): InquiryService {
    override fun inquiryParcel(carrier: String, waybillNum: String): TrackingInfo? {
        return try{
            deliveryClient.getTrackingInfo(carrier, waybillNum)
        }
        catch (e: ParcelNotFoundException){
            null
        }
        catch(e: feign.RetryableException){
            throw FailToSearchParcelException(carrier, waybillNum, e)
        }
    }
}