package org.ksoftware.lorebook.organiser

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableSet
import org.ksoftware.lorebook.tags.TagFunction
import org.ksoftware.lorebook.tags.TagModel
import tornadofx.UIComponent
import tornadofx.ViewModel

/**
 * ViewModal for a TagTree
 */
class TagOrganiserViewModel : ViewModel() {
    val treeCells = mutableListOf<UIComponent>()
    var root = TagModel()

    val headerGlyphProperty = SimpleStringProperty("Nothing")
    val headerTextProperty = SimpleStringProperty("Header")
    val subHeaderTextProperty = SimpleStringProperty("SubHeader")

    fun updateHeader(glyph: String, headerText: String, subHeaderText: String) {
        headerGlyphProperty.value = glyph
        headerTextProperty.value = headerText
        subHeaderTextProperty.value = subHeaderText

    }
}