package org.ksoftware.lorebook.tags

import com.squareup.moshi.JsonClass
import javafx.scene.paint.Color

@JsonClass(generateAdapter = true)
class TagModelJson(
    val id: String,
    val name: String,
    val color: Color,
    val children: MutableList<TagModel>,
    val showInTree: Boolean,
    val expandedInTree: Boolean
    )