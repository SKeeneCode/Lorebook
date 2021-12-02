package org.ksoftware.lorebook.main

import com.jfoenix.controls.JFXColorPicker
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.richtext.StyledSegmentTextArea
import org.ksoftware.lorebook.tags.TagModel
import tornadofx.*

/**
 * View model for a ProjectModel. When instantiated will bind to the provided ProjectModel or create a new
 * one if one isn't provided.
 */
class ProjectViewModel(model: ProjectModel = ProjectModel()) : ItemViewModel<ProjectModel>() {

    init {
        item = model
    }

    val id = bind(ProjectModel::idProperty)
    val calendars = bind(ProjectModel::calendars)

    val pageViewCache = hashMapOf<String, UIComponent>()

    val pages = bind(ProjectModel::pages)
    private var pagesBacking = listOf<PageModel>()

    val saveCoroutineScope = CoroutineScope(Dispatchers.JavaFx)

    // Parent tag for every tag in this project.
    var rootTag = TagModel()

    val taskMessage = SimpleStringProperty("No Task Running")

    // Use a shared instance of ColorPicker so recent color choices are saved.
    val colorPicker = JFXColorPicker()

    // Keeps track of the TextArea the user is currently actively editing.
    val currentRichText = SimpleObjectProperty<StyledSegmentTextArea>()

    val showOverlay = SimpleBooleanProperty(false)
    val overlayNode = SimpleObjectProperty<UIComponent>(null)

    init {
        // copies the list to its backing list after initial binding
        pagesBacking = pages.value.map { it.copy() }
    }

    override fun onCommit() {
        pagesBacking = pages.value.map { it.copy() }
    }

    fun rollBackPages() {
        pages.value = pagesBacking.map { it.copy() }.asObservable()
        super.commit(pages) {}
    }

    init {
        val tree1 = TagModel(name = "Item 1")
        val tree2 = TagModel(name = "Item 2")
        val tree3 = TagModel(name = "Item 3")
        val tree4 = TagModel(name = "Item 4")
        val tree5 = TagModel(name = "Item 5")

        val tree11 = TagModel(name = "Item 1-1")
        val tree12 = TagModel(name = "Item 1-2")
        val tree13 = TagModel(name = "Item 1-3")
        val tree14 = TagModel(name = "Item 1-4")
        val tree15 = TagModel(name = "Item 1-5")
        val tree16 = TagModel(name = "Item 1-6")

        val tree111 = TagModel(name = "Item 1-1-1")
        val tree112 = TagModel(name = "Item 1-1-2")


        val tree121 = TagModel(name = "Item 1-2-1")
        val tree122 = TagModel(name = "Item 1-2-2")

        tree1.addChild(tree11)
        tree1.addChild(tree12)
        tree1.addChild(tree13)
        tree1.addChild(tree14)
        tree1.addChild(tree15)
        tree1.addChild(tree16)

        tree11.addChild(tree111)
        tree11.addChild(tree112)
        tree12.addChild(tree121)
        tree12.addChild(tree122)

        rootTag.addChild(tree1)
        rootTag.addChild(tree2)
        rootTag.addChild(tree3)
        rootTag.addChild(tree4)
        rootTag.addChild(tree5)
    }

}