package team.sopo.parcel.domain.register

import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelCommand

interface RegisterProcessor {
    fun register(request: ParcelCommand.RegisterRequest): Parcel
}