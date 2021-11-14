package team.sopo.parcel.infrastructure.parcel.search

import org.springframework.stereotype.Component
import team.sopo.common.exception.InsufficientConditionException
import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.search.SearchProcessor
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

@Component
class SearchProcessorImpl(private val searchApiCallerList: List<SearchApiCaller>): SearchProcessor {

    override fun search(request: ParcelCommand.SearchRequest): TrackingInfo? {
        return try {
            val searchApiCaller = searchApiCaller(request)
            searchApiCaller.search(request)
        }
        catch (e: ParcelNotFoundException){
            null
        }
    }

    private fun searchApiCaller(request: ParcelCommand.SearchRequest): SearchApiCaller{
        return searchApiCallerList.stream()
            .filter{ it.support(request.searchMethod) }
            .findFirst()
            .orElseThrow { InsufficientConditionException("지원되지 않은 searchApiCaller(${request.searchMethod}) 입니다..") }
    }
}