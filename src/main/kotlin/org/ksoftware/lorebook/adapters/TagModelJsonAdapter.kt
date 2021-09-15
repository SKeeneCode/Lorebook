package org.ksoftware.lorebook.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagModelJson

/**
 * Adapter so Moshi knows how to deal with StringProperties
 */
class TagModelJsonAdapter {

    @FromJson fun fromJson(json: TagModelJson) : TagModel {
        return TagModel(
            SimpleStringProperty(json.id),
            name = json.name,
            color = json.color,
            children = FXCollections.observableSet(*json.children.toTypedArray()),
            showInTree = json.showInTree,
            expandedInTree = json.expandedInTree
        )
    }

    @ToJson fun toJson(tagModel: TagModel) : TagModelJson {
        return TagModelJson(
            tagModel.idProperty.value,
            tagModel.nameProperty.value,
            tagModel.colorProperty.value,
            tagModel.children.toMutableList(),
            tagModel.showInTree,
            tagModel.expandedInTree

        )
    }

}