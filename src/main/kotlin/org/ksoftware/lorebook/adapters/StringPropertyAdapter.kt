package org.ksoftware.lorebook.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class StringPropertyAdapter {
    @ToJson fun toJson(property: StringProperty) : String {
        return property.get()
    }

    @FromJson fun fromJson(json: String) : StringProperty {
        return SimpleStringProperty(json)
    }
}