package org.ksoftware.lorebook.tagtreeview

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.animation.Interpolator
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.SetChangeListener
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.input.TransferMode
import javafx.scene.layout.Region
import javafx.scene.text.Font
import javafx.stage.Popup
import org.ksoftware.lorebook.Styles
import org.ksoftware.lorebook.events.TagTreeRebuildRequest
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagViewModel
import org.ksoftware.lorebook.utilities.getContrastColor
import tornadofx.*
import javafx.stage.PopupWindow
import javafx.scene.input.DataFormat
import javafx.util.Duration
import javafx.scene.input.MouseButton





class TagTreeCell : View() {

    private val tagViewModel: TagViewModel by inject()
    private val tagTreeViewModel: TagTreeViewModel by inject()
    private val pageViewModel: PageViewModel by inject()
    private val projectViewModel: ProjectViewModel by inject()

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
        paddingVertical = 12
        paddingHorizontal = if (tagViewModel.item.children.isNotEmpty()) 100 else 16

        val picker = projectViewModel.colorPicker
        picker.isManaged = false
        add(picker)

        val popup = Popup()
        popup.isAutoFix = true
        popup.isAutoHide = true
        popup.isHideOnEscape = true
        popup.content.add(hbox {
            alignment = Pos.CENTER
            addClass(Styles.glyphIconHover)
            styleProperty().bind(tagViewModel.color.objectBinding {
                "-fx-background-color: ${tagViewModel.color.value?.css};" +
                        "-fx-background-radius: 12.0"
            })
            spacing = 4.0
            paddingHorizontal = 8
            paddingVertical = 4

            button {
                paddingAll = 0
                background = null
                graphic = MaterialIconView(MaterialIcon.DELETE).apply {
                    glyphSize = 26
                    fillProperty().bind(tagViewModel.color.objectBinding {
                        getContrastColor(it)
                    })
                }
                onHover {
                    cursor = Cursor.HAND
                }
                action {
                    tagTreeViewModel.deleteFunction.operate(item)
                    popup.hide()
                }
            }

            button {
                paddingAll = 0
                background = null
                graphic = MaterialIconView(MaterialIcon.PALETTE).apply {
                    glyphSize = 26
                    fillProperty().bind(tagViewModel.color.objectBinding {
                        getContrastColor(it)
                    })
                }
                onHover {
                    cursor = Cursor.HAND
                }
                action {
                    this@hbox.add(picker)
                    picker.value = tagViewModel.color.value

                    picker.setOnAction {
                        val newColor = picker.value
                        val customColors = picker.customColors
                        tagViewModel.color.value = newColor
                        if (newColor in customColors) {
                            customColors.move(newColor, 0)
                        } else {
                            customColors += newColor
                            customColors.move(newColor, 0)
                        }
                        while (customColors.size > 10) {
                            customColors.removeLast()
                        }
                        popup.hide()
                    }
                    picker.show()
                }
            }

            button {
                paddingAll = 0
                background = null
                graphic = MaterialIconView(MaterialIcon.KEYBOARD_ARROW_RIGHT).apply {
                    glyphSize = 26
                    this.addClass(Styles.glyphIconHover)
                    fillProperty().bind(tagViewModel.color.objectBinding {
                        getContrastColor(it)
                    })
                }
                removeWhen(enabled.not())
                onHover {
                    cursor = Cursor.HAND
                }
                action {
                    tagTreeViewModel.addFunction.operate(item)
                    editing.value = false
                    popup.hide()
                }
            }
        })


        textfield(tagViewModel.name) {
            prefWidth = tagViewModel.name.value.length.toDouble() * 8
            removeWhen(editing.not())
            whenVisible {
                requestFocus()
            }
            focusedProperty().onChange {
                if (!it) editing.value = false
            }
            action {
                editing.value = false
            }
        }

        label(item.nameProperty) {
            removeWhen(editing)
            textFillProperty().bind(tagViewModel.color.objectBinding {
                getContrastColor(it)
            })
            font = Font.font(12.0)
        }

        setOnMouseClicked { mouseEvent ->
            if (mouseEvent.button == MouseButton.PRIMARY) {
                if (mouseEvent.clickCount == 2) {
                        editing.value = true
                        popup.hide()
                }
            } else if (mouseEvent.button == MouseButton.SECONDARY) {
                showPopup(popup, this)
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

        setOnDragDropped { event ->
            var result = false
            val draggedTag = tagTreeViewModel.draggedTag
            draggedTag?.let {
                tagViewModel.item.addChild(draggedTag)
                event.dragboard.setContent { putString("requiresRebuild") }
                result = true
            }
            event.isDropCompleted = result
            event.consume()
        }

        setOnDragDone {
            if (it.isAccepted && it.dragboard.getContent(DataFormat.PLAIN_TEXT) == "requiresRebuild") fire(TagTreeRebuildRequest)
            it.consume()
        }
    }


    private fun showPopup(popup: Popup, ownerNode: Region) {
        val popupContent = popup.content.first()
        popupContent.opacity = 0.0
        popupContent.layoutY = 0.0

        popup.anchorLocation = PopupWindow.AnchorLocation.WINDOW_TOP_LEFT
        val anchorPoint: Point2D = ownerNode.localToScreen(
            ownerNode.width / 2,
            ownerNode.height
        )

        popup.show(
            ownerNode,
            anchorPoint.x,
            anchorPoint.y - ownerNode.height / 2
        )

        popup.anchorX -= popup.width / 2

        popupContent.layoutYProperty().animate(endValue = ownerNode.height / 2, duration = Duration(300.0), Interpolator.EASE_OUT)
        popupContent.opacityProperty().animate(endValue = 1.0, duration = Duration(300.0), Interpolator.EASE_OUT)
    }
}

