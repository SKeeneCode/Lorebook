package org.ksoftware.lorebook.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.pages.PageModelJson
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagModelJson
import tornadofx.Controller

class PageModelJsonAdapter : Controller() {

    private val projectViewModel: ProjectViewModel by inject()

    @FromJson
    fun fromJson(json: PageModelJson): PageModel {
        val tags = json.tags.map { tagID ->
            projectViewModel.rootTag.allDescendants().find {
                it.idProperty.value == tagID
            }
        }
        return PageModel(
            SimpleStringProperty(json.id),
            SimpleStringProperty(json.name),
            FXCollections.observableSet(*tags.toTypedArray())
        )

    }

    @ToJson
    fun toJson(pageModel: PageModel): PageModelJson {
        return PageModelJson(
            pageModel.idProperty.value,
            pageModel.pageName.value,
            pageModel.tagSet.map { it.idProperty.value }.toMutableList()
        )

    }

}