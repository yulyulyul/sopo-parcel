package team.sopo.domain.parcel.search

import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.trackinginfo.TrackingInfo

interface SearchApiCaller {
    fun support(searchMethod: SearchMethod): Boolean
    fun search(search: ParcelCommand.SearchRequest): TrackingInfo
}