package team.sopo.parcel.infrastructure.datasource.impl

import org.springframework.stereotype.Component
import team.sopo.parcel.application.CreateParcelService
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import team.sopo.parcel.infrastructure.RetrofitParcelInquiryService
import team.sopo.parcel.infrastructure.datasource.ParcelRemoteDataSource

@Component
class RetrofitParcelRemoteDataSource(
    private val retroInquirySvc: RetrofitParcelInquiryService,
    private val createParcelService: CreateParcelService
): ParcelRemoteDataSource {

    override fun getParcelFromRemote(carrier: String, waybillNum: String, userId: String, alias: String): Parcel {
        val trackingInfo: TrackingInfo? = retroInquirySvc.inquiryParcel(carrier, waybillNum)
        return createParcelService.createParcel(userId, waybillNum, carrier, alias, trackingInfo)
    }
}