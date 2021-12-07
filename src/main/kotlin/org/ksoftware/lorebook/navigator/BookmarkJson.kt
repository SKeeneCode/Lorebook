package org.ksoftware.lorebook.navigator

import com.squareup.moshi.JsonClass
import org.ksoftware.lorebook.utilities.Id

@JsonClass(generateAdapter = true)
data class BookmarkJson(
    val type: BookmarkNodeType,
    val folderName: String,
    val children: MutableList<BookmarkTreeNode>,
    val pageId: Id,
)