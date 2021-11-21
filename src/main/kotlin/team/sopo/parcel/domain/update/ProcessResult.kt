package team.sopo.parcel.domain.update

import team.sopo.parcel.domain.Parcel

data class ProcessResult(val updatedParcel: Parcel, val updateStatus: UpdateStatus)