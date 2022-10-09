package team.sopo.infrastructure.parcel.search

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import team.sopo.domain.parcel.carrier.Carrier
import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.search.SearchApiCaller
import team.sopo.domain.parcel.search.SearchMethod
import team.sopo.domain.parcel.trackinginfo.TrackingInfo
import team.sopo.infrastructure.parcel.tracker.sweet.SweetTrackerClient

@Component
class SweetTrackerSearchApiCaller(
    private val client: SweetTrackerClient,
    @Value("\${tracker.sweet.apikey}") private val apiKey: String
) : SearchApiCaller {

    override fun support(searchMethod: SearchMethod): Boolean {
        return searchMethod == SearchMethod.SweetTracker
    }

    override fun search(search: ParcelCommand.SearchRequest): TrackingInfo {
//        val apiId = TokenGenerator.randomCharacterWithPrefix("DELIVERY_")
        val trackingInfo = client.getTrackingInfo(apiKey, findCarrier(search.carrier), search.waybillNum)
        return trackingInfo.toTrackingInfo(search.carrier)
    }

    override fun findCarrier(code: String): String {
        return when (code) {
            Carrier.EPOST.CODE -> "01"
            Carrier.HANJINS.CODE -> "05"
            Carrier.CJ_LOGISTICS.CODE -> "04"
            Carrier.LOGEN.CODE -> "06"
            Carrier.LOTTE.CODE -> "08"
            Carrier.CHUNILPS.CODE -> "17"
            Carrier.DAESIN.CODE -> "22"
            Carrier.KDEXP.CODE -> "23"
            Carrier.CVSNET.CODE -> "24"
            Carrier.HDEXP.CODE -> "32"
            Carrier.CU_POST.CODE -> "46"
            else -> throw IllegalStateException("정의되지 않은 배송사입니다.")
        }
    }
}