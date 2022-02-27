package team.sopo.infrastructure.parcel

import org.springframework.stereotype.Component
import team.sopo.domain.parcel.Parcel
import team.sopo.domain.parcel.ParcelStore

@Component
class ParcelStoreImpl(private val repository: JpaParcelRepository) : ParcelStore {
    override fun store(parcel: Parcel): Parcel {
        return repository.save(parcel)
    }
}