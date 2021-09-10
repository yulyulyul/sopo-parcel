package team.sopo.common.extension

/**
 * Check the object is null, if it is null will call handler
 * and then return self.
 */
inline fun <T> T.guard(block: T.() -> Unit): T {
    if (this == null) block(); return this
}