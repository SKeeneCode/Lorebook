package org.ksoftware.lorebook.pages

import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TextField
import tornadofx.*

/**
 * Controller for Page views.
 */
class PageController : Controller() {

    private val pageViewModel: PageViewModel by inject()

    fun makeTabEditable(pageTab: Tab) {
        pageTab.textProperty().unbind()
        pageTab.text = ""
        val tabLabel = Label().apply {
            bind(pageViewModel.pageName)
        }
        val textField = TextField()
        pageTab.graphic = tabLabel
        tabLabel.onDoubleClick {
            textField.text = tabLabel.text
            pageTab.graphic = textField
            textField.prefWidth = tabLabel.width
            textField.selectAll()
            textField.requestFocus()
        }
        textField.setOnAction {
            pageViewModel.pageName.value = textField.text
            pageTab.graphic = tabLabel
        }
        textField.focusedProperty().onChange {
            if (!it) {
                pageViewModel.pageName.value = textField.text
                pageTab.graphic = tabLabel
            }
        }
    }
}
