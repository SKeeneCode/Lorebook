package org.ksoftware.lorebook.utilities

fun <T> List<T>.allSame() : Boolean = this.distinct().count() == 1
fun <T> List<T>.hasDifferentValues() : Boolean = this.distinct().count() > 1