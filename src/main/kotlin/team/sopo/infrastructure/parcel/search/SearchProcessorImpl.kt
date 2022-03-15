package team.sopo.infrastructure.parcel.search

import org.springframework.stereotype.Component
import team.sopo.common.extension.removeSpecialCharacter
import team.sopo.common.extension.sortProgress
import team.sopo.common.extension.verifyState
import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.search.SearchApiCaller
import team.sopo.domain.parcel.search.SearchProcessor
import team.sopo.domain.parcel.trackinginfo.TrackingInfo

@Component
class SearchProcessorImpl(private val searchApiCallerList: List<SearchApiCaller>) : SearchProcessor() {

    override fun searchApiCaller(request: ParcelCommand.SearchRequest): SearchApiCaller {
        return searchApiCallerList.stream()
            .filter { it.support(request.searchMethod) }
            .findFirst()
            .orElseThrow { IllegalStateException("지원되지 않은 searchApiCaller(${request.searchMethod}) 입니다..") }
    }

    override fun validateResult(trackingInfo: TrackingInfo) {
        trackingInfo.apply {
            removeSpecialCharacter()
            sortProgress()
            verifyState()
        }
    }
}