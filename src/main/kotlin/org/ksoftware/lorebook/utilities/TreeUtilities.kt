package org.ksoftware.lorebook.utilities

import javafx.beans.property.ObjectProperty
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableView
import javafx.scene.control.TreeView
import tornadofx.onChange
import tornadofx.populate

fun <T> TreeView<T>.selectAllChildren(root: TreeItem<T>) {
    root.children.forEach { treeItem ->
        selectionModel.select(treeItem)
        selectAllChildren(treeItem)
    }
}

fun <T> TreeView<T>.bindRoot(property: ObjectProperty<T>) {
    property.onChange {
        root = TreeItem(property.value)
    }
    root = TreeItem(property.value)
}

fun <T> TreeTableView<T>.bindRoot(property: ObjectProperty<T>) {
    property.onChange {
        root = TreeItem(property.value)
    }
    root = TreeItem(property.value)
}