package team.sopo.parcel.domain.update.policy

import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.update.UpdateResult

interface UpdatePolicyCaller {
    fun support(request: ParcelCommand.UpdateRequest): Boolean
    fun update(request: ParcelCommand.UpdateRequest): UpdateResult
}