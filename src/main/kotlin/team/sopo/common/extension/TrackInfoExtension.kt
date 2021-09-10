package team.sopo.common.extension

import team.sopo.parcel.domain.Carrier
import team.sopo.parcel.domain.DeliveryStatus
import team.sopo.parcel.domain.vo.deliverytracker.*

fun TrackingInfo.removeSpecialCharacter(): TrackingInfo
{
    return removeSpecialCharacterInTrackInfo(this)
}

fun TrackingInfo.checkProgressesSort(): TrackingInfo {
    when(this.carrier?.id) {
        Carrier.LOTTE.CODE -> {
            this.progresses.reverse()
            val trackInfoProcess = this.progresses

            val delivered = DeliveryStatus.DELIVERED.name
            if (trackInfoProcess.last()?.status?.id?.toUpperCase() == delivered) {
                this.state.id = delivered
                this.state.text = "배송완료"
            }
        }
        else -> {
            // NOTHING TO DO
        }
    }
    return this
}

private fun removeSpecialCharacterInTrackInfo(trackingInfo: TrackingInfo): TrackingInfo {
    val declaredFields = TrackingInfo::class.java.declaredFields
    for(field in declaredFields){
        field.isAccessible = true
        val trackInfoAny = field.get(trackingInfo)
        if(trackInfoAny != null){
            when(field.genericType){
                From::class.java -> {
                    removeSpecialCharacterByReflection(trackInfoAny, From::class.java)
                }
                To::class.java -> {
                    removeSpecialCharacterByReflection(trackInfoAny, To::class.java)
                }
                String::class.java -> {
                    field.set(trackInfoAny, removeSpecialCharacter(trackInfoAny as String?))
                }
                State::class.java -> {
                    removeSpecialCharacterByReflection(trackInfoAny, State::class.java)
                }
                Carrier::class.java -> {
                    removeSpecialCharacterByReflection(trackInfoAny, Carrier::class.java)
                }
            }
            if(java.util.Collection::class.java.isAssignableFrom(trackInfoAny.javaClass)){
                val listOfProgress: List<Progresses> = trackInfoAny as List<Progresses>
                listOfProgress.forEach {progresses ->
                    Progresses::class.java.declaredFields.forEach {
                        it.isAccessible = true
                        when(it.get(progresses)::class.java){
                            String::class.java -> {
                                it.set(progresses, removeSpecialCharacter(it.get(progresses) as String?))
                            }
                            Location::class.java -> {
                                removeSpecialCharacterByReflection(it.get(progresses), Location::class.java)
                            }
                            Status::class.java ->  {
                                removeSpecialCharacterByReflection(it.get(progresses), Status::class.java)
                            }
                        }
                    }
                }
            }
        }
    }
    return trackingInfo
}

private fun<T: Any> removeSpecialCharacterByReflection(obj: Any, classType: Class<T>){
    if(classType.isInstance(obj) && obj::class.java == classType){
        val objInstance = classType.cast(obj)
        classType.declaredFields.forEach {
            it.isAccessible = true
            val get = it.get(objInstance) as String?
            if(get != null) {
                it.set(objInstance, removeSpecialCharacter(get))
            }
        }
    }
}

private fun removeSpecialCharacter(str:String?): String?{
    if(str == null)
        return null
    return str.replace("\n", "").replace("\t", "").trim()
}