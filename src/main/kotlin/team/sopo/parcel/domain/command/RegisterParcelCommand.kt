package team.sopo.parcel.domain.command

import team.sopo.parcel.domain.Carrier
import team.sopo.parcel.presentation.request.RegisterParcelRequest
import java.lang.NullPointerException

class RegisterParcelCommand(
   email: String,
   request: RegisterParcelRequest
){
    val userId: String = email
    val carrier: Carrier = request.carrier ?: throw NullPointerException("CarrierEnum is null")
    val waybillNum: String = request.waybillNum
    val alias: String = request.alias
}