package org.ksoftware.lorebook.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

/**
 * Adapter so Moshi knows how to deal with StringProperties
 */
class StringPropertyAdapter {
    @ToJson fun toJson(property: StringProperty) : String {
        return property.get()
    }

    @FromJson fun fromJson(json: String) : StringProperty {
        return SimpleStringProperty(json)
    }
}