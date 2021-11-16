package team.sopo.parcel.infrastructure.register

import org.springframework.stereotype.Component
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.ParcelStore
import team.sopo.parcel.domain.register.RegisterProcessor
import team.sopo.parcel.domain.register.validator.RegisterValidator

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