package org.ksoftware.lorebook.pages

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PageModelJson(
    val id: String,
    val name: String,
    val tags: MutableList<String>
)