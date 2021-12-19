package org.ksoftware.lorebook.gallery

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.ksoftware.lorebook.media.ProjectImageModel
import org.ksoftware.lorebook.media.ProjectImageViewModel
import tornadofx.ViewModel
import java.util.function.Predicate
import kotlin.coroutines.CoroutineContext

class GalleryViewModel : ViewModel() {

    val dropLabelPrompt = SimpleStringProperty()
    val projectImageToEdit = ProjectImageViewModel()
    val compactMode = SimpleBooleanProperty(false)
    val showImageEditPrompt = SimpleBooleanProperty(true)
    val uploadedFolder = GalleryItem(folderName = SimpleStringProperty("Uploaded"))
    var filterPredicate = SimpleObjectProperty<Predicate<GalleryItem>>()
    val searchMethod = SimpleObjectProperty(ImageSearchMethod.TITLE)
    val searchString = SimpleStringProperty("")

}