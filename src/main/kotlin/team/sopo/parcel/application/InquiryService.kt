package team.sopo.parcel.application

import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

interface InquiryService {
    fun inquiryParcel(carrier: String, waybillNum: String): TrackingInfo?
}