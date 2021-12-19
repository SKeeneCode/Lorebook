package org.ksoftware.lorebook.navigator

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.pages.PageViewModel
import tornadofx.Controller

class BookmarkJsonAdapter : Controller() {

    private val projectViewModel: ProjectViewModel by inject()

    @FromJson
    fun fromJson(json: BookmarkJson) : BookmarkTreeNode {
        return BookmarkTreeNode(
            json.type,
            SimpleStringProperty(json.folderName),
            FXCollections.observableArrayList(json.children),
            PageViewModel(projectViewModel.pageModelCache[json.pageId] ?: PageModel()),
        )
    }

    @ToJson
    fun toJson(bookmarkTreeNode: BookmarkTreeNode) : BookmarkJson {
        return BookmarkJson(
            bookmarkTreeNode.type,
            bookmarkTreeNode.folderName.value,
            bookmarkTreeNode.children,
            bookmarkTreeNode.page.id.value
        )
    }
}