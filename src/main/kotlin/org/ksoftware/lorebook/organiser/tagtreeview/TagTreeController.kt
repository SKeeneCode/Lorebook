package org.ksoftware.lorebook.organiser.tagtreeview

import javafx.scene.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagViewModel
import tornadofx.*

class TagTreeController : Controller(), CoroutineScope {

    override val coroutineContext = Dispatchers.JavaFx
    private val tagTreeViewModel: TagTreeViewModel by inject()
    private val pageViewModel: PageViewModel by inject()
    private val projectViewModel: ProjectViewModel by inject()

    fun filterItemsAndRebuild(name: String, location: Node, root: TagModel) {
        filterByName(name, root)
        build(location, root)
    }

    private fun filterByName(name: String, root: TagModel): Boolean {
        // If this node is a match, mark it to be shown and check its children for further matches
        if (root.nameProperty.value.contains(name, true)) {
            root.showInTree = true
            root.children.forEach { filterByName(name, it) }
            return true
        }
        // If this node is not a match, check if any of its children are matches and if so mark it to be shown
        if (root.children.map { filterByName(name, it) }.contains(true)) {
            root.showInTree = true
            return true
        }
        // If this node is not a match nor is any of its children, mark it to be hidden
        root.showInTree = false
        return false
    }

    fun build(location: Node, root: TagModel) {
        launch {
            with(location) {
                buildTree(root)?.let { add(it.root) }
                if (tagTreeViewModel.treeCells.isEmpty()) { label("No results found") }
            }
        }

    }

    private fun buildTree(tag: TagModel): UIComponent? {
        return when {
            !tag.showInTree -> null
            tag.children.isEmpty() -> {
                val cell = find(TagTreeCell::class, Scope(tagTreeViewModel, pageViewModel, projectViewModel, TagViewModel(tag)))
                tagTreeViewModel.treeCells.add(cell)
                cell.onCreate()
                return cell
            }
            else -> {
                val cell = find(TagTreeSqueezeCell::class, Scope(tagTreeViewModel, pageViewModel, projectViewModel, TagViewModel(tag)))
                tagTreeViewModel.treeCells.add(cell)
                cell.onCreate()
                return cell
            }
        }
    }
}
