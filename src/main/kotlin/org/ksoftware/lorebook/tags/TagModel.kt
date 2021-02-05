package org.ksoftware.lorebook.tags

import com.squareup.moshi.Json
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableSet
import javafx.scene.paint.Color
import org.ksoftware.lorebook.attributes.Colored
import org.ksoftware.lorebook.attributes.Id
import org.ksoftware.lorebook.attributes.JsonModel
import org.ksoftware.lorebook.attributes.Named
import org.ksoftware.lorebook.io.Savable
import org.ksoftware.lorebook.utilities.getRandomColor
import tornadofx.*
import java.io.File
import java.util.*

//@JsonClass(generateAdapter = true)
data class TagModel(
        @Json(name = "id") override val idProperty: StringProperty = UUID.randomUUID().toString().toProperty(),
        @Transient private val name: String = "tag_name",
        @Transient private val color: Color = getRandomColor(),
        var parent: TagModel? = null,
        val children: ObservableSet<TagModel> = FXCollections.observableSet(),
        var showInTree: Boolean = true,
        var expandedInTree: Boolean = true
) : JsonModel(), Savable, Id, Named, Colored {

    @Json(name = "name") override val nameProperty: StringProperty = SimpleStringProperty(name)
    @Json(name = "color") override val colorProperty: ObjectProperty<Color> = SimpleObjectProperty(color)

    override suspend fun save(projectFolder: File, taskMessage: StringProperty) {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TagModel
        if (idProperty.value != other.idProperty.value) return false
        return true
    }

    override fun hashCode(): Int {
        return idProperty.hashCode()
    }

    private fun removeFromParentTag() {
        parent?.children?.remove(this)
    }

    fun anyParentsAre(tag: TagModel) : Boolean {
        if (parent == tag) return true
        return parent?.anyParentsAre(tag) ?: false
    }

    fun allDescendants() : MutableSet<TagModel> {
        val all = mutableSetOf<TagModel>()
        collectAllChildren(this, all)
        return all
    }

    private fun collectAllChildren(parent: TagModel, set: MutableSet<TagModel>) {
        set += parent
        for (child in parent.children) {
            collectAllChildren(child, set)
        }
    }

    fun addChild(tag: TagModel) {
        tag.removeFromParentTag()
        tag.parent = this
        children.add(tag)
    }

    override fun toString(): String {
        return "TagModel(name='$name', color=$color, children=$children, showInTree=$showInTree, expandedInTree=$expandedInTree)"
    }


}