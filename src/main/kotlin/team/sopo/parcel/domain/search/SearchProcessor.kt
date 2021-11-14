package team.sopo.parcel.domain.search

import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

interface SearchProcessor {
    fun search(request: ParcelCommand.SearchRequest): TrackingInfo?
}