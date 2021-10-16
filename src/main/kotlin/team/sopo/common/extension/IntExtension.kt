package team.sopo.common.extension

val Int.asByteArray get() =
    byteArrayOf(
            (this shr 24).toByte(),
            (this shr 16).toByte(),
            (this shr 8).toByte(),
            this.toByte())