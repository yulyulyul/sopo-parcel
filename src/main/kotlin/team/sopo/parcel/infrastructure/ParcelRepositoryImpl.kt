package team.sopo.parcel.infrastructure

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelRepository
import team.sopo.parcel.domain.vo.ParcelCntInfo
import team.sopo.parcel.infrastructure.datasource.ParcelPersistenceDataSource
import team.sopo.parcel.infrastructure.datasource.ParcelRemoteDataSource

@Component
class ParcelRepositoryImpl(
    private val persistenceDataSource: ParcelPersistenceDataSource,
    @Qualifier("feignParcelRemoteDataSource") private val remoteDataSource: ParcelRemoteDataSource
): ParcelRepository
{
    override fun getRegisterParcelCount(userId: String): Long {
        return persistenceDataSource.getRegisterParcelCount(userId)
    }

    override fun getRegisterParcelCountIn2Week(userId: String): Long {
        return persistenceDataSource.getRegisterParcelCountIn2Week(userId)
    }

    override fun getParcelHistoryWithInOneWeek(userId: String): List<Parcel>? {
       return persistenceDataSource.getParcelHistoryWithInOneWeek(userId)
    }

    override fun getCompleteParcels(pageable: Pageable, userId: String, startDate: String, endDate: String): Page<Parcel> {
        return persistenceDataSource.getCompleteParcels(pageable, userId, startDate, endDate)
    }

    override fun getParcel(userId: String, parcelId: Long): Parcel {
        return persistenceDataSource.getParcel(userId, parcelId)
    }

    override fun getRefreshedParcel(userId: String, parcelId: Long): Parcel {
        val parcel = persistenceDataSource.getParcel(userId, parcelId)
        return remoteDataSource.getRefreshedParcel(parcel, userId)
    }

    override fun getParcelFromRemote(carrier: String, waybillNum: String, userId: String, alias: String): Parcel {
        return remoteDataSource.getParcelFromRemote(carrier, waybillNum, userId, alias)
    }

    override fun getIncompleteMonth(userId: String): List<ParcelCntInfo> {
        return persistenceDataSource.getIncompleteMonth(userId)
    }

    override fun getOngoingParcels(userId: String): List<Parcel>? {
        return persistenceDataSource.getOngoingParcels(userId)
    }

    override fun save(parcel: Parcel): Parcel {
        return persistenceDataSource.saveParcel(parcel)
    }

    override fun delete(parcel: Parcel) {
        persistenceDataSource.deleteParcel(parcel)
    }

    override fun isAlreadyRegistered(userId: String, waybillNum: String, carrier: String): Boolean {
        return persistenceDataSource.isAlreadyRegistered(userId, waybillNum, carrier)
    }

    override fun isLimitCountOver(userId: String): Boolean {
        return persistenceDataSource.isLimitCountOver(userId)
    }
}