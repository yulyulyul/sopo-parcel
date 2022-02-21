package team.sopo.parcel.infrastructure

import org.springframework.stereotype.Component
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelStore

@Component
class ParcelStoreImpl(private val repository: JpaParcelRepository) : ParcelStore {
    override fun store(parcel: Parcel): Parcel {
        return repository.save(parcel)
    }
}