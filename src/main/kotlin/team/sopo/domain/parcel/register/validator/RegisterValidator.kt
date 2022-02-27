package team.sopo.domain.parcel.register.validator

import team.sopo.domain.parcel.ParcelCommand

interface RegisterValidator {
    fun validate(request: ParcelCommand.RegisterRequest)
}