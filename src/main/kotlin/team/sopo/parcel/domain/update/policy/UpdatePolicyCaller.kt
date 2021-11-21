package team.sopo.parcel.domain.update.policy

import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.update.ProcessResult
import team.sopo.parcel.domain.update.UpdateStatus

interface UpdatePolicyCaller {
    fun support(request: ParcelCommand.UpdateRequest): Boolean
    fun update(request: ParcelCommand.UpdateRequest): ProcessResult
}