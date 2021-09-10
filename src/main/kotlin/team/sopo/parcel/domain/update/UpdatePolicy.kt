package team.sopo.parcel.domain.update

abstract class UpdatePolicy {
    abstract fun run(): UpdateResult
}