package org.ksoftware.lorebook.navigator

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import dock.DetachableTabPane
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.TransferMode
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.pages.PageView
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.utilities.bindRoot
import tornadofx.*

class Bookmarks : View("Bookmarks") {

    private val projectViewModel: ProjectViewModel by inject()
    private val projectController: ProjectWorkspaceController by inject()
    private val treeview = TreeView<BookmarkTreeNode>()
    private var draggedItems = mutableListOf<BookmarkTreeNode>()
    private val folderSelected = booleanBinding(treeview.selectionModel.selectedItemProperty(), workspace.dockedComponentProperty) {
         (this != null) &&
         (this.value != null) &&
         (this.value.value != null) &&
        this.value.value.isFolder() && workspace.dockedComponentProperty.value != null
    }

    override val root = vbox {
        hbox {
            button("Page+") {
                enableWhen(folderSelected)
                action {
                    val cmp = workspace.dockedComponent
                    if (cmp is PageView) {
                        val node = BookmarkTreeNode(type = BookmarkNodeType.PAGE, page = cmp.pageViewModel)
                        val selected = treeview.selectedValue ?: return@action
                        if (selected.isFolder()) {
                            selected.addChild(node)
                        }
                    }
                }
            }
            button("Folder+") {
                action {
                    val node = BookmarkTreeNode()
                    projectViewModel.bookmarks.value.addChild(node)
                }
            }

            button("print") {
                action {
                    treeview.root.value.printRecursive()
                }
            }
        }
        with(treeview) {
            multiSelect(true)
            isShowRoot = false
            cellFormat { treeNode ->
                graphic = when (treeNode.type) {
                    BookmarkNodeType.FOLDER -> {
                        hbox(4) {
                            MaterialIconView(MaterialIcon.FOLDER).apply {
                                glyphSize = 16
                                fill = c("005199")
                                this@hbox.add(this)
                            }
                            label(treeNode.folderName)
                        }
                    }
                    BookmarkNodeType.PAGE -> {
                        hbox(4) {
                            MaterialIconView(MaterialIcon.DESCRIPTION).apply {
                                glyphSize = 16
                                fill = c("85c9dd")
                                this@hbox.add(this)
                            }
                            label(treeNode.page.pageName)
                        }
                    }
                }

                onDoubleClick {
                    if (!treeNode.isFolder()) projectController.dockPageView(treeNode.page.item)
                }

                val showHoverCss = false.toProperty()
                this.toggleClass(Styles.hoverBorderBottom, showHoverCss)

                setOnDragDetected { event ->
                    val dragboard = startDragAndDrop(TransferMode.MOVE)
                    dragboard.setContent { putImage(this@cellFormat.snapshot(null, null)) }
                    draggedItems.clear()
                    draggedItems.addAll(treeview.selectionModel.selectedItems.map { it.value })
                    event.consume()
                }

                setOnDragOver { event ->
                    if (this.item == null) return@setOnDragOver
                    if (draggedItems.isNotEmpty()) {
                        if (this == event.gestureSource) return@setOnDragOver
                        if (draggedItems.map { draggedItem -> draggedItem.containsChildRecursive(treeNode) && draggedItem.isFolder() }.contains(true)) return@setOnDragOver
                        event.acceptTransferModes(TransferMode.MOVE)
                        showHoverCss.value = true
                        event.consume()
                    } else {
                        val pageView = DetachableTabPane.DRAGGED_TAB.content.uiComponent<PageView>()
                        if (pageView != null) {
                            event.acceptTransferModes(TransferMode.MOVE)
                            showHoverCss.value = true
                        }
                        event.consume()
                    }
                }

                setOnDragExited {
                    showHoverCss.value = false
                }

                setOnDragDropped { event ->
                    var success = false
                    if (event.isAccepted) {
                        if (draggedItems.isNotEmpty()) {
                            if (treeNode.isFolder()) {
                                treeNode.addChildCollection(draggedItems)
                            } else {
                                val parent = treeNode.parent ?: return@setOnDragDropped
                                val index = parent.children.indexOf(treeNode)
                                parent.addChildCollection(draggedItems, index + 1, )
                                refresh()
                            }
                            success = true
                        } else {
                            val pageView = DetachableTabPane.DRAGGED_TAB.content.uiComponent<PageView>() ?: return@setOnDragDropped
                            val node = BookmarkTreeNode(type = BookmarkNodeType.PAGE, page = pageView.pageViewModel)
                            when (treeNode.type) {
                                BookmarkNodeType.FOLDER -> {
                                    treeNode.addChild(node, 0)
                                }
                                BookmarkNodeType.PAGE -> {
                                    val parent = treeNode.parent ?: return@setOnDragDropped
                                    val index = parent.children.indexOf(treeNode)
                                    parent.addChild(node, index + 1)
                                }
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
                    DetachableTabPane.DRAGGED_TAB = null
                    event.consume()
                }
            }

            rootProperty().onChange {
                populate { parent ->
                    val value = parent.value
                    if (parent == root) projectViewModel.bookmarks.value.children
                    else if (value.isFolder()) value.children
                    else null
                }
            }

            bindRoot(projectViewModel.bookmarks)

        }

        add(treeview)
    }
}