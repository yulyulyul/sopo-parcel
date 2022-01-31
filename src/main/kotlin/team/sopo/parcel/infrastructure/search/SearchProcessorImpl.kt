package team.sopo.parcel.infrastructure.search

import org.springframework.stereotype.Component
import team.sopo.common.exception.InsufficientConditionException
import team.sopo.common.extension.removeSpecialCharacter
import team.sopo.common.extension.sortProgress
import team.sopo.common.extension.verifyState
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.search.SearchApiCaller
import team.sopo.parcel.domain.search.SearchProcessor
import team.sopo.parcel.domain.trackinginfo.TrackingInfo

@Component
class SearchProcessorImpl(private val searchApiCallerList: List<SearchApiCaller>): SearchProcessor() {

    override fun searchApiCaller(request: ParcelCommand.SearchRequest): SearchApiCaller {
        return searchApiCallerList.stream()
            .filter{ it.support(request.searchMethod) }
            .findFirst()
            .orElseThrow { InsufficientConditionException("지원되지 않은 searchApiCaller(${request.searchMethod}) 입니다..") }
    }

    override fun validateResult(trackingInfo: TrackingInfo) {
        trackingInfo.apply {
            removeSpecialCharacter()
            sortProgress()
            verifyState()
        }
    }
}