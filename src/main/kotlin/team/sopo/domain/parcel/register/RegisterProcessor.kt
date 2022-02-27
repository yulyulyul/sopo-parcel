package team.sopo.domain.parcel.register

import team.sopo.domain.parcel.Parcel
import team.sopo.domain.parcel.ParcelCommand

interface RegisterProcessor {
    fun register(request: ParcelCommand.RegisterRequest): Parcel
}