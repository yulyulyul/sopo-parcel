package team.sopo.domain.parcel.search

import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.trackinginfo.TrackingInfo

abstract class SearchProcessor {
    fun search(request: ParcelCommand.SearchRequest): TrackingInfo? {
        return try {
            val searchApiCaller = searchApiCaller(request)
            searchApiCaller.search(request).apply { validateResult(this) }
        } catch (e: ParcelNotFoundException) {
            null
        }
    }

    abstract fun searchApiCaller(request: ParcelCommand.SearchRequest): SearchApiCaller
    abstract fun validateResult(trackingInfo: TrackingInfo)
}