package team.sopo.infrastructure.parcel.search

import org.springframework.stereotype.Component
import team.sopo.common.exception.FailToSearchParcelException
import team.sopo.common.tracing.DeliveryTracing
import team.sopo.common.tracing.DeliveryTrackerRepository
import team.sopo.common.util.TokenGenerator
import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.search.SearchApiCaller
import team.sopo.domain.parcel.search.SearchMethod
import team.sopo.domain.parcel.trackinginfo.TrackingInfo
import team.sopo.infrastructure.parcel.DeliveryTrackerClient

@Component
class DeliveryTrackerSearchApiCaller(private val deliveryTrackerClient: DeliveryTrackerClient, private val repository: DeliveryTrackerRepository) :
    SearchApiCaller {

    override fun support(searchMethod: SearchMethod): Boolean {
        return searchMethod == SearchMethod.DeliveryTracker
    }

    override fun search(search: ParcelCommand.SearchRequest): TrackingInfo {
        val apiId = TokenGenerator.randomCharacterWithPrefix("DELIVERY_")

        return try {
            repository.saveTrackingPersonalData(search.toTrackingPersonalData(apiId))
            deliveryTrackerClient.getTrackingInfo(apiId, search.carrier.CODE, search.waybillNum)
        } catch (e: feign.RetryableException) {
            repository.saveError(apiId, e::class.simpleName.toString(), e.message)
            logContent(apiId)

            throw FailToSearchParcelException(search.carrier.CODE, search.waybillNum, e)
        }
    }

    private fun logContent(apiId: String) {
        val content = repository.getContentByApiId(apiId)
        DeliveryTracing(content).trace()
    }
}