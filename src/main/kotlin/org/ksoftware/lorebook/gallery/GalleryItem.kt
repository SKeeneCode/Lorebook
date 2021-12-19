package org.ksoftware.lorebook.gallery

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.ksoftware.lorebook.media.ProjectImageViewModel

class GalleryItem(
    val type: GalleryItemType = GalleryItemType.FOLDER,
    val folderName: StringProperty = SimpleStringProperty("Image Folder"),
    val projectImage: ProjectImageViewModel = ProjectImageViewModel(),
    var parent: GalleryItem? = null,
    val children: ObservableList<GalleryItem> = FXCollections.observableArrayList()
) {

    fun isFolder() : Boolean = type == GalleryItemType.FOLDER

    fun filterNameByString(filterString: String) : Boolean {
        if (children.map { it.filterNameByString(filterString) }.contains(true)) { return true }

        if (isFolder()) {
            if (folderName.value.contains(filterString, true)) return true
        } else {
            if(projectImage.title.value.contains(filterString, true)) return true
        }
        return false
    }

    fun filterByTag(filterString: String) : Boolean {
        if (children.map { it.filterByTag(filterString) }.contains(true)) { return true }
        if (isFolder()) {
            if (folderName.value.contains(filterString, true)) return true
        } else {
            if(projectImage.imageTags.map { it.nameProperty.value.contains(filterString) }.contains(true)) return true
        }
        return false
    }


    /**
     * returns the depth of this GalleryItem. 0 = child of root.
     */
    fun depth() : Int {
        val parent = parent ?: return 0
        return getDepthTraverse(parent, 0)
    }

    private fun getDepthTraverse(root: GalleryItem, depth: Int) : Int {
        var localDepth = depth
        val parent = root.parent ?: return depth
        localDepth++
        return getDepthTraverse(parent, localDepth)
    }

    private fun removeFromParent() = parent?.children?.remove(this)

    fun addChild(item: GalleryItem, index: Int = 0) {
        item.removeFromParent()
        if (index <= children.size) {
            children.add(index, item)
        } else {
            children.add(item)
        }
        println("adding child to ${this.hashCode()} it now has ${this.children.size}")
        item.parent = this
    }

    fun addChildCollection(items: MutableSet<GalleryItem>, index: Int = 0) {
        items.removeIf { item -> item.allParents().any { parent -> items.contains(parent) } }
        items.forEach { addChild(it, index) }
    }

    private fun allParents() : Set<GalleryItem> {
        val parentList = mutableSetOf<GalleryItem>()
        return traverseParents(this, parentList)
    }

    private fun traverseParents(item: GalleryItem, parentList: MutableSet<GalleryItem>) : Set<GalleryItem> {
        val parent = item.parent ?: return parentList
        parentList.add(parent)
        return traverseParents(parent, parentList)
    }

    fun containsChildRecursive(node: GalleryItem) : Boolean {
        if (children.contains(node)) return true
        children.filter { it.type == GalleryItemType.FOLDER }.forEach {
            return it.containsChildRecursive(node)
        }
        return false
    }

    override fun toString(): String {
        return "GalleryItem(type=$type, folderName=$folderName, children=$children, projectImage=$projectImage)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GalleryItem

        if (type != other.type) return false
        if (folderName != other.folderName) return false
        if (children != other.children) return false
        if (projectImage != other.projectImage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + folderName.hashCode()
        result = 31 * result + children.hashCode()
        result = 31 * result + projectImage.hashCode()
        return result
    }


}

enum class GalleryItemType {
    FOLDER,
    IMAGE
}