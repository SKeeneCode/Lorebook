package org.ksoftware.lorebook.tags

fun interface TagFunction {
    fun operate(tag: TagModel) : Boolean
}