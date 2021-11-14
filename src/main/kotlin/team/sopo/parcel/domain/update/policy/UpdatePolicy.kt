package team.sopo.parcel.domain.update.policy

import team.sopo.parcel.domain.update.UpdateResult

interface UpdatePolicy {
    fun run(): UpdateResult
}