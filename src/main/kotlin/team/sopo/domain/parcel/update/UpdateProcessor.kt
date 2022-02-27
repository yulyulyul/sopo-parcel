package team.sopo.domain.parcel.update

import team.sopo.domain.parcel.ParcelCommand

interface UpdateProcessor {
    fun update(request: ParcelCommand.UpdateRequest): ProcessResult
}