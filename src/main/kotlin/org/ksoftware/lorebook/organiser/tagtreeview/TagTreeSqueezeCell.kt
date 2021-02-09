package org.ksoftware.lorebook.organiser.tagtreeview

import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import org.ksoftware.lorebook.Styles
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagViewModel
import tornadofx.*

class TagTreeSqueezeCell : View("My View") {

    private val tagViewModel: TagViewModel by inject()
    private val tagTreeViewModel: TagTreeViewModel by inject()
    private val tagTreeController: TagTreeController by inject()
    private val item = tagViewModel.item

    override val root = titledpane(collapsible = true) {
        addClass(Styles.tagTitleRegion)
        val cell = find(TagTreeCell::class)
        cell.onCreate()
        tagTreeViewModel.treeCells.add(cell)
        val newTagField = TextField().apply {
            minWidth = 80.0
            prefWidth = 80.0
            maxWidth = 200.0
            promptText = "New tag.."
            textProperty().onChange { prefWidth = it?.length?.times(8.0) ?: 80.0 }
            removeWhen(this@titledpane.hoverProperty().not().and(focusedProperty().not()))
        }
        graphic = HBox(cell.root, newTagField).apply {
            spacing = 4.0
        }
        isExpanded = item.expandedInTree
        this.expandedProperty().onChange {
            item.expandedInTree = it
        }
        vbox {
            style {
                padding = CssBox(0.px, 0.px, 0.px, 10.px)
            }
            flowpane {
                newTagField.setOnAction {
                    val new = TagModel(name = newTagField.text ?: "", color = tagViewModel.color.value)
                    newTagField.text = ""
                    tagViewModel.item.addChild(new)
                    tagTreeController.build(this, new)
                }
                translateX = 14.0
                this.hgap = 5.0
                this.vgap = 5.0
                for (childWithNoChildren in item.children.filter { it.children.isEmpty() })
                    tagTreeController.build(this, childWithNoChildren)
            }
            for (childWithChildren in item.children.filter { it.children.isNotEmpty() }) {
                tagTreeController.build(this, childWithChildren)
            }
        }
    }
}