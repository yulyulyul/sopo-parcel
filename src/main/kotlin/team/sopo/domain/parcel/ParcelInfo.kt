package team.sopo.domain.parcel

import team.sopo.domain.parcel.carrier.Carrier
import team.sopo.domain.parcel.trackinginfo.TrackingInfo
import java.io.Serializable
import java.time.LocalDateTime

class ParcelInfo {
    class Main(
        var parcelId: Long,
        var userId: Long,
        var waybillNum: String,
        var carrier: Carrier,
        var alias: String,
        var inquiryResult: TrackingInfo? = null,
        var inquiryHash: String? = null,
        var deliveryStatus: Parcel.DeliveryStatus? = null,
        var regDte: LocalDateTime,
        var arrivalDte: LocalDateTime? = null,
        var auditDte: LocalDateTime,
        var status: Int,
        var reported: Boolean
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
    ) {
        companion object {
            fun getEmptyData(): MonthlyPageInfo = MonthlyPageInfo(
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

    data class CarrierStatus(
        val carrier: String,
        val name: String,
        val available: Boolean
    ) : Serializable
}