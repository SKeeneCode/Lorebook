package org.ksoftware.lorebook.tagtreeview

import com.jfoenix.controls.JFXColorPicker
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.SetChangeListener
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.input.TransferMode
import javafx.scene.text.Font
import org.ksoftware.lorebook.Styles
import org.ksoftware.lorebook.events.TagTreeRebuildRequest
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagViewModel
import org.ksoftware.lorebook.utilities.getContrastColor
import tornadofx.*


class TagTreeCell : View() {

    private val tagViewModel: TagViewModel by inject()
    private val tagTreeViewModel: TagTreeViewModel by inject()
    private val pageViewModel: PageViewModel by inject()
    private val item = tagViewModel.item
    private val enabled = SimpleBooleanProperty(true)
    private val editing = SimpleBooleanProperty(false)
    private val setChangeListener: SetChangeListener<TagModel> = SetChangeListener {
        if (it.wasAdded() && it.elementAdded == item) {
            root.opacity = 0.4
            enabled.value = false
        } else if (it.wasRemoved() && it.elementRemoved == item) {
            root.opacity = 1.0
            enabled.value = true
        }
    }


    override fun onCreate() {
        pageViewModel.tags.value.addListener(setChangeListener)
        if (pageViewModel.tags.value.contains(item)) {
            root.opacity = 0.4
            enabled.value = false
        } else {
            root.opacity = 1.0
            enabled.value = true
        }
    }

    override fun onDelete() {
        pageViewModel.tags.value.removeListener(setChangeListener)
    }

    override val root = hbox {
            alignment = Pos.CENTER
            styleProperty().bind(tagViewModel.color.objectBinding {
                "-fx-background-color: ${tagViewModel.color.value?.css};" +
                        "-fx-background-radius: 12.0"
            })
            spacing = 2.0
            paddingHorizontal = 8
            paddingVertical = 4
            onDoubleClick {
                if (enabled.value) editing.value = true
            }
            contextmenu {
                addClass(Styles.transparentContextMenu)
                val picker = JFXColorPicker(tagViewModel.color.value)
                picker.bind(tagViewModel.color)
                item("", graphic = picker)
            }
            button {
                paddingAll = 0
                background = null
                graphic = FontAwesomeIconView(FontAwesomeIcon.TIMES_CIRCLE).apply {
                    glyphSize = 22
                    fill = getContrastColor(item.colorProperty.value)
                    fillProperty().bind(tagViewModel.color.objectBinding {
                        getContrastColor(it)
                    })
                }
                onHover {
                    cursor = Cursor.HAND
                }
                hiddenWhen((this@hbox.hoverProperty().not()).and(editing.not()))
                action {
                    tagTreeViewModel.deleteFunction.operate(item)
                }
            }
            textfield(tagViewModel.name) {
                prefWidth = tagViewModel.name.value.length.toDouble() * 8
                removeWhen(editing.not())
                whenVisible {
                    requestFocus()
                }
                action {
                    editing.value = false
                }
            }
            label(item.nameProperty) {
                removeWhen(editing)
                textFill = getContrastColor(tagViewModel.color.value)
                textFillProperty().bind(tagViewModel.color.objectBinding {
                    getContrastColor(it)
                })
                font = Font.font(12.0)
            }
            button {
                paddingAll = 0
                background = null
                graphic = FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE).apply {
                    glyphSize = 22
                    fillProperty().bind(tagViewModel.color.objectBinding {
                        getContrastColor(it)
                    })
                }
                onHover {
                    cursor = Cursor.HAND
                }
                hiddenWhen((this@hbox.hoverProperty().not()).or(enabled.not()).and(editing.not()))
                action {
                    tagTreeViewModel.addFunction.operate(item)
                    editing.value = false
                }
        }

        setOnDragDetected {
            val db = startDragAndDrop(TransferMode.MOVE)
            db.setContent { putImage(this@hbox.snapshot(null, null)) }
            tagTreeViewModel.draggedTag = tagViewModel.item
            it.consume()
        }

        setOnDragOver {
            val draggedTag = tagTreeViewModel.draggedTag
            if (it.gestureSource != this && draggedTag != null && !tagViewModel.item.anyParentsAre(draggedTag)) {
                it.acceptTransferModes(TransferMode.MOVE)
            }
            it.consume()
        }

        setOnDragDropped {
            var result = false
            val draggedTag = tagTreeViewModel.draggedTag
            draggedTag?.let {
                tagViewModel.item.addChild(draggedTag)
                result = true
            }
            it.isDropCompleted = result
            it.consume()
        }

        setOnDragDone {
            if (it.isAccepted) fire(TagTreeRebuildRequest)
            it.consume()
        }

    }
}