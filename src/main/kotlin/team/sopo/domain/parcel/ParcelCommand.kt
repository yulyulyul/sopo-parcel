package team.sopo.domain.parcel

import org.springframework.data.domain.Pageable
import team.sopo.domain.parcel.search.SearchMethod
import team.sopo.domain.parcel.trackinginfo.TrackingInfo

class ParcelCommand {
    data class GetParcel(
        val userToken: String,
        val parcelId: Long
    )

    data class GetParcels(
        val userToken: String,
        val parcelIds: List<Long>
    )

    data class Reporting(
        val userToken: String,
        val parcelIds: List<Long>
    )

    data class GetOngoingParcels(val userToken: String)

    data class GetCompleteParcels(
        val userToken: String,
        val inquiryDate: String,
        val itemCnt: Int,
        val pageable: Pageable
    )

    data class GetMonthlyParcelCnt(val userToken: String)

    data class GetMonthlyPageInfo(
        val userToken: String,
        val cursorDate: String?
        ){
        fun isFirstPage() = cursorDate.isNullOrBlank()
    }

    data class RegisterParcel(
        val userToken: String,
        val carrier: String,
        val waybillNum: String,
        val alias: String,
        val trackingInfo: TrackingInfo? = null
    ) {
        fun toEntity(trackingInfo: TrackingInfo?): Parcel {
            return Parcel(userToken, waybillNum, carrier, alias, trackingInfo)
        }

        fun toSearchRequest(): SearchRequest {
            return SearchRequest(userToken, carrier, waybillNum)
        }

        fun toRegisterRequest(parcel: Parcel): RegisterRequest {
            return RegisterRequest(userToken, carrier, waybillNum, parcel)
        }
    }

    data class ChangeParcelAlias(
        val userToken: String,
        val parcelId: Long,
        val alias: String
    )

    data class TrackingPersonalData(
        val userToken: String,
        val apiId: String,
        val carrier: Carrier,
        val waybillNum: String
    )

    data class DeleteParcel(
        val userToken: String,
        val parcelIds: List<Long>
    )

    data class SearchRequest(
        val userToken: String,
        val carrier: String,
        val waybillNum: String,
        val searchMethod: SearchMethod = SearchMethod.SopoTracker
    ) {
        fun toTrackingPersonalData(apiId: String): TrackingPersonalData {
            return TrackingPersonalData(userToken, apiId, Carrier.getCarrierByCode(carrier), waybillNum)
        }
    }

    data class RegisterRequest(
        val userToken: String,
        val carrier: String,
        val waybillNum: String,
        val parcel: Parcel
    )

    data class PushRequest(
        val userToken: String,
        val parcelIds: List<Long>
    )

    data class DeviceAwakenRequest(val topic: String)

    data class GetUsageInfo(val userToken: String)

    data class UpdateRequest(
        val originalParcel: Parcel,
        val refreshedParcel: Parcel
    )

    data class SingleRefresh(
        val userToken: String,
        val parcelId: Long
    ) {
        fun toEntity(trackingInfo: TrackingInfo?, originalParcel: Parcel): Parcel {
            return Parcel(
                userToken,
                originalParcel.waybillNum,
                originalParcel.carrier,
                originalParcel.alias,
                trackingInfo
            )
        }

        fun toSearchRequest(originalParcel: Parcel): SearchRequest {
            return SearchRequest(
                originalParcel.userToken,
                originalParcel.carrier,
                originalParcel.waybillNum
            )
        }

        fun toUpdateRequest(originalParcel: Parcel, refreshedParcel: Parcel): UpdateRequest {
            return UpdateRequest(originalParcel, refreshedParcel)
        }
    }

    data class EntireRefresh(val userToken: String) {
        fun toRefreshRequest(parcelId: Long): SingleRefresh {
            return SingleRefresh(userToken, parcelId)
        }
    }
}
