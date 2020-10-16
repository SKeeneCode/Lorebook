package org.ksoftware.lorebook.main

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import org.ksoftware.lorebook.pages.Page
import org.ksoftware.lorebook.pages.PageModel

/**
 * Model class for a project. Properties in this class should (almost) never be bound to the user interface.
 * Instead use a ProjectViewModel.
 */
data class ProjectModel(
        val pages: ObservableList<PageModel> = FXCollections.observableArrayList(),
        val dockedPages: ObservableMap<String, Page> = FXCollections.observableHashMap()
) {
//
//    // list of all page models in this project
//    val pages: ObservableList<PageModel> = FXCollections.observableArrayList()
//
//    // list of all docked page views
//    val dockedPages: ObservableMap<String, Page> = FXCollections.observableHashMap()

}