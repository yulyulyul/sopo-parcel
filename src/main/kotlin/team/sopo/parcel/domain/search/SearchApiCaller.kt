package team.sopo.parcel.domain.search

import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.trackinginfo.TrackingInfo

interface SearchApiCaller {
    fun support(searchMethod: SearchMethod): Boolean
    fun search(search: ParcelCommand.SearchRequest): TrackingInfo
}