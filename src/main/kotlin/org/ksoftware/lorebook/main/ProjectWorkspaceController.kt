package org.ksoftware.lorebook.main

import com.squareup.moshi.JsonClass
import dock.DetachableTabPane
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import mu.KotlinLogging
import org.ksoftware.lorebook.actions.SaveProjectAction
import org.ksoftware.lorebook.io.Savable
import org.ksoftware.lorebook.io.IOController
import org.ksoftware.lorebook.navigator.BookmarkTreeNode
import org.ksoftware.lorebook.nodes.TextController
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.pages.PageView
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import org.ksoftware.lorebook.settings.ProjectSettingsViewModel
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.utilities.Id
import tornadofx.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Controller for the project workspace.
 */
class ProjectWorkspaceController : Controller(), Savable {


    private val textController: TextController by inject(FX.defaultScope)
    private val projectViewModel: ProjectViewModel by inject()
    private val projectSettingsViewModel: ProjectSettingsViewModel by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()
    private val ioController: IOController by inject()
    private val saveProjectActor: SendChannel<SaveProjectAction> = createSaveActor()

    /**
     * Creates a new page and docks it in the provided workspace
     */
    fun dockNewPage(placement: Pos = Pos.CENTER) {
        val newPage = PageModel()
        projectViewModel.pageModelCache.putIfAbsent(newPage.idProperty.value, newPage)
        dockPageView(newPage, placement)
    }

     private fun dockFromPageID(id: Id, placement: Pos = Pos.CENTER) : PageView {

        val pageView = projectViewModel.pageViewCache[id]
        if (pageView != null) {
            workspace.dock(pageView, placement = placement)
            return pageView
        }

        val pageToDock = projectViewModel.pageModelCache[id] ?: PageModel(SimpleStringProperty(id))
        return dockPageView(pageToDock, placement)
    }

    /**
     * Docks the PageView associated with the provided PageModel in the workspace.
     * Checks the cache of PageViews to see if the pages view is in there.
     * If not, creates a new page view in a new scope and docks it.
     */
    fun dockPageView(page: PageModel, placement: Pos = Pos.CENTER): PageView {
        val pageToDock = projectViewModel.pageViewCache[page.idProperty.value] ?: find(
            Scope(
                PageViewModel(page),
                projectViewModel,
                projectSettingsViewModel,
                toolbarViewModal
            )
        )
        projectViewModel.pageViewCache.putIfAbsent(page.idProperty.value, pageToDock)
        projectViewModel.pageModelCache.putIfAbsent(page.idProperty.value, page)
        workspace.dock(pageToDock, placement = placement)
        return pageToDock
    }

    // --------------------------------------- //
    //                 SAVING                  //
    // --------------------------------------- //

    override suspend fun save(projectFolder: File, ioController: IOController) {
        saveOpenPageStructure(projectFolder)
        saveProjectTags(projectFolder)
        saveProjectBookmarks(projectFolder)
    }

    fun saveProject(stage: Stage) {
        saveProjectActor.trySend(SaveProjectAction(projectViewModel.item, stage))
    }

    /**
     * Creates an actor that processes one save action at a time. Will block any further save actions until the
     * current one has finished processing.
     */
    private fun createSaveActor() = projectViewModel.saveCoroutineScope.actor<SaveProjectAction> {
        for (saveAction in channel) {
            val projectSaveFolder = askUserForProjectSaveFolder(saveAction.stage)
            projectSaveFolder?.let {
                save(it, ioController)
                saveAction.project.save(it, ioController)
            }
        }
    }

    private fun saveProjectBookmarks(saveLocation: File) {
        val bookmarkFile = File(saveLocation.path + "/bookmarks.txt")
        val br = BufferedWriter(FileWriter(bookmarkFile))
        val bookmarks = projectViewModel.bookmarks.value
        val json = ioController.moshi.adapter(BookmarkTreeNode::class.java).indent("    ").toJson(bookmarks)
        br.write(json)
        br.close()
    }

    private fun saveOpenPageStructure(saveLocation: File) {
        val topSplitPane = findTopmostSplitPane(workspace.detachableTabPane)
        if (topSplitPane != null) {
            val tree = traverseSplitPaneTree(topSplitPane)
            val mainFile = File(saveLocation.path + "/openPages.txt")
            val br = BufferedWriter(FileWriter(mainFile))
            val json = ioController.moshi.adapter(TreeNode::class.java).indent("    ").toJson(tree)
            br.write(json)
            br.close()
        }
    }

    private fun saveProjectTags(saveLocation: File) {
            val rootTag = projectViewModel.rootTag
            val mainFile = File(saveLocation.path + "/projectTags.txt")
            val br = BufferedWriter(FileWriter(mainFile))
            val json = ioController.moshi.adapter(TagModel::class.java).indent("    ").toJson(rootTag)
            br.write(json)
            br.close()
        }

    private fun traverseSplitPaneTree(sp: SplitPane): TreeNode {
        val spt = TreeNode(type = NodeType.SplitPane)
        sp.items.forEach { node ->
            when (node) {
                is SplitPane -> {
                    spt.children += traverseSplitPaneTree(node)
                }
                is DetachableTabPane -> {
                    val treeNode = TreeNode(
                        type = NodeType.TabPane,
                        pages = node.tabs.map { it.content.id }.toMutableList()
                    )
                    spt.children += treeNode
                }
            }
        }
        spt.dividerPositions += sp.dividerPositions.toMutableList()
        spt.split = sp.orientation
        return spt
    }

    /**
     * Traverses up the scenegraph from the supplied parent and returns the top most found SplitPane
     */
    private fun findTopmostSplitPane(node: Parent): SplitPane? {
        var sp: SplitPane? = null
        var parent: Parent? = node.parent
        while (parent != null) {
            if (parent is SplitPane) {
                sp = parent
            }
            parent = parent.parent
        }
        return sp
    }

    /**
     * Used to build a data tree structure to represent the tree of SplitPanes and DetachableTabPanes that
     * make up the pages open in the workspace.
     */
    @JsonClass(generateAdapter = true)
    data class TreeNode(
        val type: NodeType,
        val dividerPositions: MutableList<Double> = mutableListOf(),
        val pages: MutableList<String> = mutableListOf(),
        val children: MutableList<TreeNode> = mutableListOf(),
        var split: Orientation = Orientation.HORIZONTAL
    )

    enum class NodeType {
        SplitPane,
        TabPane
    }


    private fun askUserForProjectSaveFolder(stage: Stage): File? {
        val directoryChooser = DirectoryChooser()
        directoryChooser.initialDirectory = File("/")
        return directoryChooser.showDialog(stage)
    }


    // --------------------------------------- //
    //                 LOADING                 //
    // --------------------------------------- //

    fun loadProject() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.initialDirectory = File("/")
        val projectFolder = askUserForProjectSaveFolder(primaryStage)
        projectFolder?.let {
            val fileList = it.list() ?: return
            println(fileList.toString())
            if (fileList.contains("project.txt")) {
                loadProjectTags(projectFolder)
                loadPages(projectFolder)
                loadOpenPages(projectFolder)
                loadProjectBookmarks(projectFolder)
            }
        }
    }

    private fun loadProjectBookmarks(projectFolder: File) {
        val json = Files.readString(Path.of(projectFolder.path + "/bookmarks.txt"));
        projectViewModel.bookmarks.value = ioController.moshi.adapter(BookmarkTreeNode::class.java).indent("    ").fromJson(json)!!
        projectViewModel.bookmarks.value.validateBookmarkParents()
    }

    private fun loadProjectTags(projectFolder: File) {
        val json = Files.readString(Path.of(projectFolder.path + "/projectTags.txt"));
        projectViewModel.rootTag = ioController.moshi.adapter(TagModel::class.java).indent("    ").fromJson(json)!!
        projectViewModel.rootTag.validateTagsParents()
    }

    private fun loadPages(projectFolder: File) {
        val pages = PageModel.loadProjectPages(projectFolder, ioController)
        pages.forEach {
            projectViewModel.pageModelCache.putIfAbsent(it.idProperty.value, it)
        }
    }

    private fun loadOpenPages(projectFolder: File) {
        val json = Files.readString(Path.of(projectFolder.path + "/openPages.txt"));
        val tree = ioController.moshi.adapter(TreeNode::class.java).indent("    ").fromJson(json)
        val tempTabs = mutableListOf<Tab>()
        tree?.let { traverseTree(it, workspace.detachableTabPane, tempTabs) }
        tempTabs.forEach { it.close() }
    }

    private fun traverseTree(tree: TreeNode, parentTabPane: DetachableTabPane, list: MutableList<Tab>) {
        var tabPane = parentTabPane
        if (tree.type == NodeType.SplitPane && workspace.dockedComponent == null) {
            val cmp = dockFromPageID(UUID.randomUUID().toString(), Pos.CENTER)
            val tab = workspace.findTabFromUIComponent(cmp)
            tab?.let {list.add(tab) }
        }
        tree.children.forEach { child ->
            if (child.type == NodeType.TabPane) {
                val temp = tabPane
                workspace.focusedTabPane.value = tabPane
                traverseDock(child.pages.removeFirst(), tree.split)
                child.pages.forEach {
                    dockFromPageID(it, Pos.CENTER)
                }
                tabPane = temp
                workspace.focusedTabPane.value = tabPane
            } else if (child.type == NodeType.SplitPane) {
                val temp = tabPane
                val cmp = traverseDock(UUID.randomUUID().toString(), tree.split)
                tabPane = workspace.focusedTabPane.value
                traverseTree(child, tabPane, list)
                val tab = workspace.findTabFromUIComponent(cmp)
                tab?.let { list.add(tab) }
                tabPane = temp
                workspace.focusedTabPane.value = tabPane
            }
        }
        tabPane.parentSplitPane.setDividerPositions(*tree.dividerPositions.toDoubleArray())
    }

    private fun traverseDock(id: String, orientation: Orientation): UIComponent {
        val cmp = if (orientation == Orientation.VERTICAL) {
            dockFromPageID(id, Pos.TOP_CENTER)
        } else {
            dockFromPageID(id, Pos.CENTER_LEFT)
        }
        return cmp
    }

    // --------------------------------------- //
    //                 OVERLAY                 //
    // --------------------------------------- //

    /**
     * Causes the overlay to appear with this UIComponent centered.
     */
    fun openOverlayWith(uiComponent: UIComponent) {
        projectViewModel.overlayNode.value = uiComponent
    }

    /**
     * Removes the UIComponent from the overlay and closes the overlay.
     */
    fun closeOverlay() {
        projectViewModel.overlayNode.value = null
    }


    // --------------------------------------- //
    //                RICHTEXT                 //
    // --------------------------------------- //

    fun connectToolbarViewModalToRichTextAreas() {
        toolbarViewModal.updateParagraphTrigger.onChange {
            textController.updateParagraphStyleInSelection(projectViewModel.currentRichText.value) { parStyle ->
                parStyle.updateWith(
                    toolbarViewModal.createParagraphStyle()
                )
            }
            projectViewModel.currentRichText.value.requestFocus()
        }
        toolbarViewModal.increaseIndentTrigger.onChange {
            textController.updateParagraphStyleInSelection(projectViewModel.currentRichText.value) { parStyle -> parStyle.increaseIndent() }
            projectViewModel.currentRichText.value.requestFocus()
        }
        toolbarViewModal.decreaseIndentTrigger.onChange {
            textController.updateParagraphStyleInSelection(projectViewModel.currentRichText.value) { parStyle -> parStyle.decreaseIndent() }
            projectViewModel.currentRichText.value.requestFocus()
        }
        toolbarViewModal.updateTextTrigger.onChange {
            val style = toolbarViewModal.createTextStyle()
            textController.updateStyleInSelection(projectViewModel.currentRichText.value, style)
            projectViewModel.currentRichText.value.textInsertionStyle = style
            projectViewModel.currentRichText.value.requestFocus()
        }
    }

}

