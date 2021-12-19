package org.ksoftware.lorebook.controls

import javafx.beans.property.SetProperty
import javafx.beans.property.SimpleSetProperty
import org.ksoftware.lorebook.tags.TagModel
import tornadofx.*

class AutoCompleteTagFieldViewModal : ViewModel() {

    val comparisonTagSet = SimpleSetProperty<TagModel>()
}