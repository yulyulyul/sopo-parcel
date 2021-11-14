package team.sopo.parcel.domain.register.validator

import team.sopo.parcel.domain.ParcelCommand

interface RegisterValidator {
    fun validate(request: ParcelCommand.RegisterRequest)
}