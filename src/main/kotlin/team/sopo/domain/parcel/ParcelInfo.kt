package team.sopo.domain.parcel

import team.sopo.domain.parcel.trackinginfo.TrackingInfo
import java.time.LocalDateTime

class ParcelInfo {
    class Main(
        var parcelId: Long? = null,
        var userId: Long,
        var waybillNum: String? = null,
        var carrier: Carrier? = null,
        var alias: String? = null,
        var inquiryResult: TrackingInfo? = null,
        var inquiryHash: String? = null,
        var deliveryStatus: Parcel.DeliveryStatus? = null,
        var regDte: LocalDateTime? = null,
        var arrivalDte: LocalDateTime? = null,
        var auditDte: LocalDateTime? = null,
        var status: Int? = null,
        var reported: Boolean? = null
    )

    class RefreshedParcel(
        val parcel: Main,
        val isUpdated: Boolean
    )

    class ParcelUsage(
        val registeredCountIn2Week: Long,
        val totalRegisteredCount: Long
    )

    data class MonthlyPageInfo(
        val hasPrevious: Boolean,
        val previousDate: String?,
        val hasNext: Boolean,
        val nextDate: String?,
        val cursorDate: String?
    ){
        companion object{
            fun getEmptyData(): MonthlyPageInfo = ParcelInfo.MonthlyPageInfo(
                hasPrevious = false,
                previousDate = null,
                hasNext = false,
                nextDate = null,
                cursorDate = null
            )
        }
    }

    class MonthlyParcelCnt(_time: String, _count: Long) {
        val time = _time
        val count = _count

        override fun toString(): String {
            return "{time:$time, count:$count}"
        }
    }
}