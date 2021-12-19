package org.ksoftware.lorebook.utilities

fun <T> MutableList<T>.addAllToFront(elements: Collection<T>) : Boolean = this.addAll(0, elements)