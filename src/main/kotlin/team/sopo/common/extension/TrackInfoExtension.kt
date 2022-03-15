package team.sopo.common.extension

import team.sopo.domain.parcel.Carrier
import team.sopo.domain.parcel.Parcel
import team.sopo.domain.parcel.trackinginfo.*

fun TrackingInfo.removeSpecialCharacter(): TrackingInfo {
    return removeSpecialCharacterInTrackInfo(this)
}

fun TrackingInfo.sortProgress() {
    return this.progresses.sortWith(compareBy {
        when (it?.status?.id?.uppercase()) {
            Parcel.DeliveryStatus.NOT_REGISTERED.name -> 0
            Parcel.DeliveryStatus.AT_PICKUP.name -> 1
            Parcel.DeliveryStatus.IN_TRANSIT.name -> 2
            Parcel.DeliveryStatus.OUT_FOR_DELIVERY.name -> 3
            Parcel.DeliveryStatus.DELIVERED.name -> 4
            else -> 999
        }
    })
}

fun TrackingInfo.verifyState() {
    if (this.progresses.isEmpty()) {
        return
    }
    this.progresses.last()?.status?.apply {
        this@verifyState.state.id = this.id
        this@verifyState.state.text = this.text
    }
}

private fun removeSpecialCharacterInTrackInfo(trackingInfo: TrackingInfo): TrackingInfo {
    val declaredFields = TrackingInfo::class.java.declaredFields
    for (field in declaredFields) {
        field.isAccessible = true
        val trackInfoAny = field.get(trackingInfo)
        if (trackInfoAny != null) {
            when (field.genericType) {
                From::class.java -> {
                    removeSpecialCharacterByReflection(trackInfoAny, From::class.java)
                }
                To::class.java -> {
                    removeSpecialCharacterByReflection(trackInfoAny, To::class.java)
                }
                TrackingInfo::item::class.java -> {
                    removeSpecialCharacterByReflection(trackInfoAny, TrackingInfo::item::class.java)
                }
                State::class.java -> {
                    removeSpecialCharacterByReflection(trackInfoAny, State::class.java)
                }
                Carrier::class.java -> {
                    removeSpecialCharacterByReflection(trackInfoAny, Carrier::class.java)
                }
            }
            if (java.util.Collection::class.java.isAssignableFrom(trackInfoAny.javaClass)) {
                val listOfProgress: List<Progresses> = trackInfoAny as List<Progresses>
                listOfProgress.forEach { progresses ->
                    Progresses::class.java.declaredFields.forEach {
                        it.isAccessible = true
                        when (it.get(progresses)::class.java) {
                            String::class.java -> {
                                it.set(progresses, removeSpecialCharacter(it.get(progresses) as String?))
                            }
                            Location::class.java -> {
                                removeSpecialCharacterByReflection(it.get(progresses), Location::class.java)
                            }
                            Status::class.java -> {
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

private fun <T : Any> removeSpecialCharacterByReflection(obj: Any, classType: Class<T>) {
    if (classType.isInstance(obj) && obj::class.java == classType) {
        val objInstance = classType.cast(obj)
        classType.declaredFields.forEach {
            it.isAccessible = true
            val get = it.get(objInstance) as String?
            if (get != null) {
                it.set(objInstance, removeSpecialCharacter(get))
            }
        }
    }
}

private fun removeSpecialCharacter(str: String?): String? {
    if (str == null)
        return null
    if (str.isEmpty())
        return str
    return str.replace("\n", "").replace("\t", "").trim()
}