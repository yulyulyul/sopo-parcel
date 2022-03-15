package team.sopo.domain.parcel

import org.springframework.data.domain.Pageable
import team.sopo.domain.parcel.search.SearchMethod
import team.sopo.domain.parcel.trackinginfo.TrackingInfo

class ParcelCommand {
    data class GetParcel(
        val userId: Long,
        val parcelId: Long
    )

    data class GetParcels(
        val userId: Long,
        val parcelIds: List<Long>
    )

    data class GetOngoingParcels(val userId: Long)

    data class GetCompleteParcels(
        val userId: Long,
        val inquiryDate: String,
        val itemCnt: Int,
        val pageable: Pageable
    )

    data class GetMonthlyParcelCnt(val userId: Long)

    data class RegisterParcel(
        val userId: Long,
        val carrier: String,
        val waybillNum: String,
        val alias: String,
        val trackingInfo: TrackingInfo? = null
    ) {
        fun toEntity(trackingInfo: TrackingInfo?): Parcel {
            return Parcel(trackingInfo, userId, waybillNum, carrier, alias)
        }

        fun toSearchRequest(): SearchRequest {
            return SearchRequest(userId, carrier, waybillNum)
        }

        fun toRegisterRequest(parcel: Parcel): RegisterRequest {
            return RegisterRequest(userId, carrier, waybillNum, parcel)
        }
    }

    data class ChangeParcelAlias(
        val userId: Long,
        val parcelId: Long,
        val alias: String
    )

    data class TrackingPersonalData(
        val userId: Long,
        val apiId: String,
        val carrier: Carrier,
        val waybillNum: String
    )

    data class DeleteParcel(
        val userId: Long,
        val parcelIds: List<Long>
    )

    data class SearchRequest(
        val userId: Long,
        val carrier: String,
        val waybillNum: String,
        val searchMethod: SearchMethod = SearchMethod.SweetTracker
    ) {
        fun toTrackingPersonalData(apiId: String): TrackingPersonalData {
            return TrackingPersonalData(userId, apiId, Carrier.getCarrierByCode(carrier), waybillNum)
        }
    }

    data class RegisterRequest(
        val userId: Long,
        val carrier: String,
        val waybillNum: String,
        val parcel: Parcel
    )

    data class PushRequest(
        val userId: Long,
        val parcelIds: List<Long>
    )

    data class DeviceAwakenRequest(val topic: String)

    data class GetUsageInfo(val userId: Long)

    data class UpdateRequest(
        val originalParcel: Parcel,
        val refreshedParcel: Parcel
    )

    data class SingleRefresh(
        val userId: Long,
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
                originalParcel.carrier,
                originalParcel.waybillNum
            )
        }

        fun toUpdateRequest(originalParcel: Parcel, refreshedParcel: Parcel): UpdateRequest {
            return UpdateRequest(originalParcel, refreshedParcel)
        }
    }

    data class EntireRefresh(val userId: Long) {
        fun toRefreshRequest(parcelId: Long): SingleRefresh {
            return SingleRefresh(userId, parcelId)
        }
    }
}
