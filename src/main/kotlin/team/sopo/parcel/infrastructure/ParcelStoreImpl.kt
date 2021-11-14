package team.sopo.parcel.infrastructure

import org.springframework.stereotype.Service
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelStore

@Service
class ParcelStoreImpl(private val repository: JpaParcelRepository): ParcelStore {
    override fun store(parcel: Parcel): Parcel {
        return repository.save(parcel)
    }
}