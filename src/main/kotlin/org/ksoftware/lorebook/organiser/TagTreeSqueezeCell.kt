package org.ksoftware.lorebook.organiser

import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagViewModel
import tornadofx.*
import java.util.*

class TagTreeSqueezeCell : View("My View") {

    private val tagViewModel: TagViewModel by inject()
    private val tagOrganiserViewModel: TagOrganiserViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val tagOrganiserController: TagOrganiserController by inject()

    private val item = tagViewModel.item

    override val root = titledpane(collapsible = true) {
        addClass(Styles.tagTitleRegion)
        val cell = find(TagTreeCell::class)
        cell.onCreate()
        tagOrganiserViewModel.treeCells.add(cell)
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
                    clear()
                    for (childWithNoChildren in item.children.filter { it.children.isEmpty() }.sortedBy { it::nameProperty.get().value.lowercase(
                        Locale.getDefault()
                    ) })
                        tagOrganiserController.build(this, childWithNoChildren)
                }
                translateX = 14.0
                this.hgap = 5.0
                this.vgap = 5.0
                // First add in childless children.
                for (childWithNoChildren in item.children.filter { it.children.isEmpty() }.sortedBy { it::nameProperty.get().value.lowercase(
                    Locale.getDefault()
                ) })
                    tagOrganiserController.build(this, childWithNoChildren)
            }
            // Then add in children with children, as they will become their own TagTreeSqueezeCell.
            for (childWithChildren in item.children.filter { it.children.isNotEmpty() }.sortedBy { it::nameProperty.get().value.lowercase(
                Locale.getDefault()
            ) }) {
                tagOrganiserController.build(this, childWithChildren)
            }
        }
    }
}
