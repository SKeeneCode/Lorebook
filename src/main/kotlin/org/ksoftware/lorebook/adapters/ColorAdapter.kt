package org.ksoftware.lorebook.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
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