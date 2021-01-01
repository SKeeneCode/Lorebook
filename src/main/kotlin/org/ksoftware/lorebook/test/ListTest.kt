package org.ksoftware.lorebook.test

import javafx.scene.control.TreeItem
import tornadofx.*

class ListTest : View("My View") {


    override val root = pane {
        val myItems = mutableListOf<MyTreeItem>()

        val tree1 = MyTreeItem("Item 1")
        val tree2 = MyTreeItem("Item 2")
        val tree3 = MyTreeItem("Item 3")
        val tree4 = MyTreeItem("Item 4")
        val tree5 = MyTreeItem("Item 5")

        val tree11 = MyTreeItem("Item 1-1")
        val tree12 = MyTreeItem("Item 1-2")
        val tree13 = MyTreeItem("Item 1-3")
        val tree14 = MyTreeItem("Item 1-4")
        val tree15 = MyTreeItem("Item 1-5")
        val tree16 = MyTreeItem("Item 1-6")

        val tree111 = MyTreeItem("Item 1-1-1")
        val tree112 = MyTreeItem("Item 1-1-2")


        val tree121 = MyTreeItem("Item 1-2-1")
        val tree122 = MyTreeItem("Item 1-2-2")

        tree1.children.add(tree11)
        tree1.children.add(tree12)
        tree1.children.add(tree13)
        tree1.children.add(tree14)
        tree1.children.add(tree15)
        tree1.children.add(tree16)

        tree11.children.add(tree111)
        tree11.children.add(tree112)
        tree12.children.add(tree121)
        tree12.children.add(tree122)

        myItems.add(tree1)
        myItems.add(tree2)
        myItems.add(tree3)
        myItems.add(tree4)
        myItems.add(tree5)


        treeview<MyTreeItem> {
            root = TreeItem(MyTreeItem("Items"))
            cellFormat { text = it.name }
            cellFactory
            populate { parent ->
                if (parent == root) myItems else parent.value.children
            }
        }
    }
}

data class MyTreeItem(
        val name: String,
        var children: MutableList<MyTreeItem> = mutableListOf()
)
