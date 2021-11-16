package team.sopo.parcel.infrastructure.search

import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.search.SearchMethod
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

interface SearchApiCaller {
    fun support(searchMethod: SearchMethod): Boolean
    fun search(search: ParcelCommand.SearchRequest): TrackingInfo
}