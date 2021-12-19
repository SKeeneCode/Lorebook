package org.ksoftware.lorebook.media

import javafx.beans.property.SetProperty
import javafx.beans.property.SimpleSetProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.utilities.IdProperty
import org.ksoftware.lorebook.utilities.ImageProperty
import java.util.*

data class ProjectImageModel(
    val idProperty: IdProperty = SimpleStringProperty(UUID.randomUUID().toString()),
    val titleProperty: StringProperty = SimpleStringProperty(),
    val captionProperty: StringProperty = SimpleStringProperty(),
    val fileURIProperty: StringProperty = SimpleStringProperty(),
    val imageProperty: ImageProperty = ImageProperty(),
    val imageTagsProperty: SetProperty<TagModel> = SimpleSetProperty()
)