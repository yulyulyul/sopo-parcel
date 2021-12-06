package team.sopo.parcel.domain

import org.springframework.data.domain.Pageable
import team.sopo.parcel.domain.search.SearchMethod
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

class ParcelCommand {
    data class GetParcel(
        val userId: String,
        val parcelId: Long
    )

    data class GetOngoingParcels(val userId: String)

    data class GetCompleteParcels(
        val userId: String,
        val inquiryDate: String,
        val pageable: Pageable
    )

    data class GetMonthlyParcelCnt(val userId: String)

    data class RegisterParcel(
        val userId: String,
        val carrier: Carrier,
        val waybillNum: String,
        val alias: String,
        val trackingInfo: TrackingInfo? = null
    ) {
        fun toEntity(trackingInfo: TrackingInfo?): Parcel {
            return Parcel(trackingInfo, userId, waybillNum, carrier.CODE, alias)
        }

        fun toSearchRequest(): SearchRequest {
            return SearchRequest(userId, carrier, waybillNum)
        }

        fun toRegisterRequest(parcel: Parcel): RegisterRequest {
            return RegisterRequest(userId, carrier, waybillNum, parcel)
        }
    }

    data class ChangeParcelAlias(
        val userId: String,
        val parcelId: Long,
        val alias: String
    )

    data class DeleteParcel(
        val userId: String,
        val parcelIds: List<Long>
    )

    data class SearchRequest(
        val userId: String,
        val carrier: Carrier,
        val waybillNum: String,
        val searchMethod: SearchMethod = SearchMethod.DeliveryTracker
    )

    data class RegisterRequest(
        val userId: String,
        val carrier: Carrier,
        val waybillNum: String,
        val parcel: Parcel
    )

    data class PushRequest(
        val userId: String,
        val parcelIds: List<Long>
    )

    data class DeviceAwakenRequest(val topic: String)

    data class GetUsageInfo(val userId: String)

    data class UpdateRequest(
        val originalParcel: Parcel,
        val refreshedParcel: Parcel
    )

    data class SingleRefresh(
        val userId: String,
        val parcelId: Long
    ) {
        fun toEntity(trackingInfo: TrackingInfo?, originalParcel: Parcel): Parcel {
            return Parcel(
                trackingInfo,
                originalParcel.userId,
                originalParcel.waybillNum,
                originalParcel.carrier,
                originalParcel.alias
            )
        }

        fun toSearchRequest(originalParcel: Parcel): SearchRequest {
            return SearchRequest(
                originalParcel.userId,
                Carrier.getCarrierByCode(originalParcel.carrier),
                originalParcel.waybillNum
            )
        }

        fun toUpdateRequest(originalParcel: Parcel, refreshedParcel: Parcel): UpdateRequest {
            return UpdateRequest(originalParcel, refreshedParcel)
        }
    }

    data class EntireRefresh(val userId: String) {
        fun toRefreshRequest(parcelId: Long): SingleRefresh {
            return SingleRefresh(userId, parcelId)
        }
    }
}
