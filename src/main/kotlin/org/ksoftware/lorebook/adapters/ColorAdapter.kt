package org.ksoftware.lorebook.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import javafx.scene.paint.Color
import tornadofx.*

/**
 * Adapter so Moshi knows how to deal with StringProperties
 */
class ColorAdapter {
    @ToJson fun toJson(color: Color) : String {
        return color.css
    }

    @FromJson fun fromJson(json: String) : Color {
        return c(json)
    }
}