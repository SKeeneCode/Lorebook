package org.ksoftware.lorebook.organiser

import javafx.collections.ObservableSet
import javafx.scene.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.tagflow.TagFlowCell
import org.ksoftware.lorebook.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagViewModel
import tornadofx.*

class TagOrganiserController : Controller(), CoroutineScope {

    override val coroutineContext = Dispatchers.JavaFx
    private val tagOrganiserViewModel: TagOrganiserViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val projectViewModel: ProjectViewModel by inject()



    fun filterItemsAndRebuild(name: String, location: Node, root: TagModel) {
        filterByName(name, root)
        build(location, root)
    }

    fun disabledCellIfInSet(set: ObservableSet<TagModel>) {
        traverseDisable(projectViewModel.rootTag, set)
    }

    private fun traverseDisable(root: TagModel, set: ObservableSet<TagModel>) {
        root.disabled.value = set.contains(root)
        for (childTag in root.children) {
            traverseDisable(childTag, set)
        }
    }

    /**
     * Traverses the tree of Tags, marking whether they should be shown on not when the tree is next rebuilt.
     */
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

    /**
     * Constructs a tree of tags at the given location.
     */
    fun build(location: Node, root: TagModel) {
        launch {
            with(location) {
                buildTree(root)?.let { add(it.root) }
                if (tagOrganiserViewModel.treeCells.isEmpty()) { label("No results found") }
            }
        }

    }

    private fun buildTree(tag: TagModel): UIComponent? {
        return when {
            // If this tag is marked not to be shown, don't build this branch of the tree.
            !tag.showInTree -> null
            // If this tag is marked, but has no children, add in a normal TagTreeCell
            tag.children.isEmpty() -> {
                val cell = find(TagTreeCell::class, Scope(tagOrganiserViewModel, tagFlowViewModel, projectViewModel, TagViewModel(tag)))
                tagOrganiserViewModel.treeCells.add(cell)
                cell.onCreate()
                return cell
            }
            // If this tag is marked and has children, add in a TagTreeSqueezeCell, which will continue to build its children.
            else -> {
                val cell = find(TagTreeSqueezeCell::class, Scope(tagOrganiserViewModel, tagFlowViewModel, projectViewModel, TagViewModel(tag)))
                tagOrganiserViewModel.treeCells.add(cell)
                cell.onCreate()
                return cell
            }
        }
    }
}
