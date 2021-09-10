package team.sopo.common.infra

interface Datasource<T: Any, F: Any> {
    fun get(id: F): T
    fun save(data: T)
}