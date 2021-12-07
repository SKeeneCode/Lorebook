package org.ksoftware.lorebook.navigator

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tags.TagModel

data class BookmarkTreeNode(
    val type: BookmarkNodeType = BookmarkNodeType.FOLDER,
    val folderName: SimpleStringProperty = SimpleStringProperty("Folder Name"),
    val children: ObservableList<BookmarkTreeNode> = FXCollections.observableArrayList(),
    val page: PageViewModel = PageViewModel(),
    var parent: BookmarkTreeNode? = null
) {

    fun isFolder() : Boolean {
        return type == BookmarkNodeType.FOLDER
    }

    private fun removeFromParent() {
        parent?.children?.remove(this)
    }

    fun addChild(item: BookmarkTreeNode, index: Int = 0) {
        item.removeFromParent()
        if (index <= children.size) {
            children.add(index, item)
        } else {
            children.add(item)
        }
        item.parent = this
    }

    fun addChildCollection(items: MutableList<BookmarkTreeNode>, index: Int = 0) {
        // if we're already trying to add an items parent folder we don't need to worry about it
        println("adding ${items.size} to $index , where there is ${children.size} children")
        items.removeIf { item -> item.allParents().any { parent -> items.contains(parent) } }
        items.forEach { addChild(it, index) }
    }

    fun validateBookmarkParents() {
        for (child in children) {
            child.parent = this
            child.validateBookmarkParents()
        }
    }

    private fun allParents() : List<BookmarkTreeNode> {
        val parentList = mutableListOf<BookmarkTreeNode>()
        return traverseParents(this, parentList)
    }

    private fun traverseParents(item: BookmarkTreeNode, parentList: MutableList<BookmarkTreeNode>) : List<BookmarkTreeNode> {
        val parent = item.parent ?: return parentList
        parentList.add(parent)
        return traverseParents(parent, parentList)
    }

    fun containsChildRecursive(node: BookmarkTreeNode) : Boolean {
        if (children.contains(node)) return true
        children.filter { it.type == BookmarkNodeType.FOLDER }.forEach {
            return it.containsChildRecursive(node)
        }
        return false
    }

    fun printRecursive() {
        println(this.toString())
        children.forEach { println(it.toString()) }
    }

    override fun toString(): String {
        return "BookmarkTreeNode(folderName=$folderName, isFolder=$type, children=${children.size})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookmarkTreeNode

        if (type != other.type) return false
        if (folderName != other.folderName) return false
        if (children != other.children) return false
        if (page != other.page) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + folderName.hashCode()
        result = 31 * result + children.hashCode()
        result = 31 * result + page.hashCode()
        return result
    }


}

enum class BookmarkNodeType {
    FOLDER,
    PAGE
}
