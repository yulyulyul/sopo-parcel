package team.sopo.parcel.infrastructure.parcel.search

import org.springframework.stereotype.Component
import team.sopo.common.exception.FailToSearchParcelException
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.search.SearchMethod
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import team.sopo.parcel.infrastructure.DeliveryTrackerClient

@Component
class DeliveryTrackerSearchApiCaller(private val deliveryTrackerClient: DeliveryTrackerClient): SearchApiCaller {
    override fun support(searchMethod: SearchMethod): Boolean {
        return searchMethod == SearchMethod.DeliveryTracker
    }

    override fun search(search: ParcelCommand.SearchRequest): TrackingInfo {
        return try{
            deliveryTrackerClient.getTrackingInfo(search.carrier.CODE, search.waybillNum)
        }
        catch(e: feign.RetryableException){
            throw FailToSearchParcelException(search.carrier.CODE, search.waybillNum, e)
        }
    }
}