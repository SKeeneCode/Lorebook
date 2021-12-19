package org.ksoftware.lorebook.gallery

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.control.TreeTableRow
import javafx.scene.control.TreeTableView
import javafx.scene.input.MouseButton
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.ksoftware.lorebook.controls.AutoCompleteTagFieldViewModal
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.organiser.TagOrganiserController
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.tagflow.TagFlowCellCompact
import org.ksoftware.lorebook.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.tags.TagViewModel
import org.ksoftware.lorebook.utilities.bindRoot
import org.ksoftware.lorebook.utilities.forceHeight
import org.ksoftware.lorebook.utilities.forceSize
import tornadofx.*

class GalleryTreeTableView : View() {

    private val projectViewModel: ProjectViewModel by inject()
    private val projectController: ProjectWorkspaceController by inject()
    private val tagFieldViewModel: AutoCompleteTagFieldViewModal by inject()
    private val tagOrganiserController: TagOrganiserController by inject()

    private val galleryViewModel: GalleryViewModel by inject()
    private val imageTable = TreeTableView<GalleryItem>()
    private var draggedItems = mutableSetOf<GalleryItem>()

    init {
        projectViewModel.galleryRoot.value.addChild(galleryViewModel.uploadedFolder)
    }

    override val root = treetableview<GalleryItem> {
        isShowRoot = false
        toggleClass(Styles.compactTreeCells, galleryViewModel.compactMode)
        vgrow = Priority.ALWAYS
        multiSelect(true)
        column<GalleryItem, GalleryItem>("Title") { ReadOnlyObjectWrapper(it.value.value) }
            .cellFormat { galleryItem ->
                graphic = when (galleryItem.type) {
                    GalleryItemType.FOLDER -> {
                        hbox(4) {
                            paddingLeft = 18.0 + 12.0 * galleryItem.depth()
                            val editing = SimpleBooleanProperty(false)
                            alignment = Pos.CENTER_LEFT
                            maxHeight = 24.0
                            MaterialIconView(MaterialIcon.FOLDER).apply {
                                glyphSize = 16
                                fill = c("005199")
                                this@hbox.add(this)
                            }
                            label(galleryItem.folderName) {
                                removeWhen(editing)
                            }
                            textfield(galleryItem.folderName) {
                                prefHeightProperty().bind(this@hbox.heightProperty())
                                prefWidth = 100.0
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
                            setOnMouseClicked { mouseEvent ->
                                if (mouseEvent.button == MouseButton.PRIMARY) {
                                    if (mouseEvent.clickCount == 2) {
                                        editing.value = true
                                    }
                                }
                            }
                            contextmenu {
                                item("Rename Folder") {
                                    graphic = MaterialIconView(MaterialIcon.EDIT).apply {
                                        glyphSize = 18
                                        fill = Color.FIREBRICK
                                    }
                                    action {
                                        editing.value = true
                                    }
                                }
                                item("Delete Folder") {
                                    graphic = MaterialIconView(MaterialIcon.CLEAR).apply {
                                        glyphSize = 18
                                        fill = Color.RED
                                    }
                                }
                            }
                        }
                    }
                    GalleryItemType.IMAGE -> {
                        hbox(4) {
                            paddingLeft = 18.0 + 12.0 * galleryItem.depth()
                            MaterialIconView(MaterialIcon.IMAGE).apply {
                                glyphSize = 16
                                fill = c("85c9dd")
                                this@hbox.add(this)
                            }
                            label(galleryItem.projectImage.title)
                        }
                    }
                }
            }

        column<GalleryItem, GalleryItem>("Thumbnail") { ReadOnlyObjectWrapper(it.value.value) }
            .cellFormat { galleryItem ->
                alignment = Pos.CENTER
                graphic = imageview(galleryItem.projectImage.image) {
                    isPreserveRatio = true
                    galleryViewModel.compactMode.onChange { compactMode ->
                        if (compactMode) {
                            fitWidthProperty().cleanBind(20.0.toProperty())
                        } else {
                            fitWidthProperty().cleanBind(80.toProperty())
                        }
                    }
                    if (galleryViewModel.compactMode.value) {
                        fitWidthProperty().cleanBind(20.0.toProperty())
                    } else {
                        fitWidthProperty().cleanBind(80.toProperty())
                    }
                }
            }


        column<GalleryItem, GalleryItem>("Caption") { ReadOnlyObjectWrapper(it.value.value) }
            .cellFormat { galleryItem ->
                graphic = label {
                    prefWidthProperty().bind(this@cellFormat.widthProperty())
                    bind(stringBinding(galleryItem.projectImage.caption) {
                        if (this.value == null) {
                            ""
                        } else {
                            var string = this.value.take(100)
                            if (string.length >= 100) string += "..."
                            string
                        }
                    })
                    isWrapText = true
                }
            }

        column<GalleryItem, GalleryItem>("Tags") { ReadOnlyObjectWrapper(it.value.value) }
            .cellFormat { galleryItem ->
                this.prefWidth = 200.0
                graphic = when (galleryItem.type) {
                    GalleryItemType.FOLDER -> pane { forceSize(0.0, 0.0) }
                    GalleryItemType.IMAGE -> scrollpane {
                        galleryViewModel.compactMode.onChange { compactMode -> if (compactMode) { forceHeight(20.0) } else { forceHeight(60.0) } }
                        if (galleryViewModel.compactMode.value) { forceHeight(20.0) } else { forceHeight(60.0) }
                        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                        flowpane {
                            prefWrapLengthProperty().bind(this@cellFormat.widthProperty().minus(20))
                            hgap = 4.0
                            vgap = 4.0

                            val tagFlowView = TagFlowViewModel()
                            tagFlowView.tagSet.value = galleryItem.projectImage.imageTags
                            tagFlowView.tagToNodeConverter = { tag ->
                                find(TagFlowCellCompact::class, Scope(tagFlowView, TagViewModel(tag))).root
                            }
                            bindChildren(galleryItem.projectImage.imageTags, tagFlowView.tagToNodeConverter)
                        }
                    }
                }

            }


        setRowFactory {
            TreeTableRow<GalleryItem?>().apply {

                val showHoverCss = false.toProperty()
                this.toggleClass(Styles.hoverBorderBottom, showHoverCss)

               itemProperty().onChange {
                   if (it != null) {
                       if (it.isFolder()) { forceHeight(24.0) } else {
                           if (galleryViewModel.compactMode.value) forceHeight(24.0) else forceHeight(80.0)
                       }
                   }
               }
                galleryViewModel.compactMode.onChange { compactMode ->
                    if (compactMode) {
                        forceHeight(24.0)
                    } else {
                        val galleryItem = item
                        if (galleryItem != null && !galleryItem.isFolder()) forceHeight(80.0)
                    }
                }
                onLeftClick {
                    val galleryItem = this.item ?: return@onLeftClick
                    galleryViewModel.projectImageToEdit.rebind { item = galleryItem.projectImage.item }
                    galleryViewModel.showImageEditPrompt.value = galleryItem.isFolder()
                    tagFieldViewModel.comparisonTagSet.value = galleryItem.projectImage.imageTags

                }

                setOnDragDetected { event ->
                    val dragboard = startDragAndDrop(TransferMode.MOVE)
                    dragboard.setContent { putImage(this@apply.snapshot(null, null)) }
                    draggedItems.clear()
                    draggedItems.addAll(this@treetableview.selectionModel.selectedItems.map { it.value })
                    // prevent dragging the uploaded folder
                    draggedItems.remove(galleryViewModel.uploadedFolder)
                    event.consume()
                }

                setOnDragOver { event ->
                    val item = this.item ?: return@setOnDragOver
                    if (draggedItems.isNotEmpty()) {
                        if (this == event.gestureSource) return@setOnDragOver
                        if (item == galleryViewModel.uploadedFolder) return@setOnDragOver
                        if (draggedItems.map { draggedItem -> draggedItem.containsChildRecursive(item) && draggedItem.isFolder() }.contains(true)) return@setOnDragOver
                        event.acceptTransferModes(TransferMode.MOVE)
                        showHoverCss.value = true
                        event.consume()
                    }
                }


                setOnDragExited {
                    showHoverCss.value = false
                }

                setOnDragDropped { event ->
                    val galleryItem = item!!
                    var success = false
                    if (event.isAccepted) {
                        if (draggedItems.isNotEmpty()) {
                            if (galleryItem.isFolder()) {
                                galleryItem.addChildCollection(draggedItems)
                                refresh()
                            } else {
                                val parent = galleryItem.parent ?: return@setOnDragDropped
                                val index = parent.children.indexOf(galleryItem)
                                parent.addChildCollection(draggedItems, index + 1, )
                                refresh()
                            }
                            success = true
                        }
                    }
                    draggedItems.clear()
                    event.isDropCompleted = success
                    event.consume()
                }

                setOnDragDone { event ->
                    println("DRAG DONE")
                    draggedItems.clear()
                    event.consume()
                }

            }
        }


        rootProperty().onChange {
            populate { parent ->
                val value = parent.value
                if (parent == root) FilteredList(projectViewModel.galleryRoot.value.children).apply {
                    predicate = galleryViewModel.filterPredicate.value
                    galleryViewModel.filterPredicate.onChange { it ->
                        predicate = it
                    }
                }
                else if (value.isFolder()) FilteredList(value.children).apply {
                    predicate = galleryViewModel.filterPredicate.value
                    galleryViewModel.filterPredicate.onChange { it ->
                        predicate = it
                    }
                }
                else null
            }
        }

        bindRoot(projectViewModel.galleryRoot)


        root.isExpanded = true
        root.children.forEach { it.isExpanded = true }

    }
}