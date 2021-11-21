package team.sopo.parcel.domain.update

import team.sopo.parcel.domain.ParcelCommand

interface UpdateProcessor {
    fun update(request: ParcelCommand.UpdateRequest): ProcessResult
}