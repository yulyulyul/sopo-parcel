package team.sopo.common.tracing

interface TracingRepository<T: Any> {
    fun getContent(): T
}