package team.sopo.parcel.domain.search

import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.trackinginfo.TrackingInfo

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