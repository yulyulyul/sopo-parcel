package team.sopo.parcel.domain.update

class NoChange: UpdatePolicy() {

    override fun run(): UpdateResult {
            return UpdateResult.NO_CHANGE
    }
}