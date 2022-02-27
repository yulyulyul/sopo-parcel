package team.sopo.domain.parcel.update.policy

import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.update.ProcessResult

interface UpdatePolicyCaller {
    fun support(request: ParcelCommand.UpdateRequest): Boolean
    fun update(request: ParcelCommand.UpdateRequest): ProcessResult
}