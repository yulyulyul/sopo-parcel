package team.sopo.domain.parcel.update

import team.sopo.domain.parcel.Parcel

data class ProcessResult(val updatedParcel: Parcel, val updateStatus: UpdateStatus)