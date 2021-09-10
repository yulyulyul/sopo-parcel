package team.sopo.common.extension

fun ByteArray.exactlyEqual(other: ByteArray): Boolean
{
    if(this.size != other.size) return false

    for(i in this.indices)
    {
        if(this[i] != other[i]) return false
    }
    return true
}

fun Byte.toPositiveInt() = toInt() and 0xFF