package org.ksoftware.lorebook.gallery

import com.sksamuel.scrimage.ImmutableImage
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.media.ProjectImageModel
import org.ksoftware.lorebook.media.ProjectImageViewModel
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.utilities.ImageProperty
import tornadofx.Controller
import tornadofx.onChange
import java.io.File
import java.util.function.Predicate
import kotlin.random.Random

class GalleryController : Controller() {

    private val projectViewModel: ProjectViewModel by inject()
    private val galleryViewModel: GalleryViewModel by inject()
    val processedImages = SimpleIntegerProperty(0)
    val imageIOScope = CoroutineScope(Dispatchers.Main)

    init {
        galleryViewModel.searchString.onChange { filterGallery() }
        galleryViewModel.searchMethod.onChange { filterGallery() }
    }

    private fun filterGallery() {
        val searchMethod = galleryViewModel.searchMethod.value
        val searchString = galleryViewModel.searchString.value
        galleryViewModel.filterPredicate.value = when (searchMethod) {
            ImageSearchMethod.TITLE -> Predicate { galleryItem -> galleryItem.filterNameByString(searchString) }
            ImageSearchMethod.TAGS -> Predicate { galleryItem -> galleryItem.filterByTag(searchString) }
            else -> Predicate { true }
        }
    }

    fun processImage(file: File) {
        imageIOScope.launch {
            val immutableImage = ImmutableImage.loader().fromFile(file)
            val writableImage = WritableImage(immutableImage.width, immutableImage.height)
            val javafxImage = SwingFXUtils.toFXImage(immutableImage.awt(), writableImage)
            val projectImage = ProjectImageModel(
                titleProperty = SimpleStringProperty(file.name),
                fileURIProperty = SimpleStringProperty(file.toURI().toString()),
                imageProperty = ImageProperty(javafxImage)
            )
            projectViewModel.projectImages.value.putIfAbsent(projectImage.idProperty.value, projectImage)
            galleryViewModel.uploadedFolder.addChild(
                GalleryItem(
                    GalleryItemType.IMAGE,
                    projectImage = ProjectImageViewModel(projectImage)
                )
            )
            processedImages.value += 1
        }
    }


}