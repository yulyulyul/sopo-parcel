package team.sopo.infrastructure.parcel.register

import org.springframework.stereotype.Component
import team.sopo.domain.parcel.Parcel
import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.ParcelStore
import team.sopo.domain.parcel.register.RegisterProcessor
import team.sopo.domain.parcel.register.validator.RegisterValidator

@Component
class RegisterProcessorImpl(
    private val parcelStore: ParcelStore,
    private val validatorList: List<RegisterValidator>
) : RegisterProcessor {

    override fun register(request: ParcelCommand.RegisterRequest): Parcel {
        validatorList.forEach { validator -> validator.validate(request) }
        return parcelStore.store(request.parcel)
    }
}