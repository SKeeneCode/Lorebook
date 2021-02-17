package org.ksoftware.lorebook.pages

import javafx.scene.control.Tab
import tornadofx.*

/**
 * Controller for Page views.
 */
class PageController : Controller() {

    private val pageViewModel: PageViewModel by inject()

    fun makeTabEditable(tab: Tab) {
        tab.apply {
            tab.textProperty().unbind()
            tab.text = ""
            val tabLabel = label(pageViewModel.pageName)
            val textField = textfield()
            tab.graphic = tabLabel
            tabLabel.onDoubleClick {
                textField.text = tabLabel.text
                tab.graphic = textField
                textField.prefWidth = tabLabel.width
                textField.selectAll()
                textField.requestFocus()
            }
            textField.setOnAction {
                pageViewModel.pageName.value = textField.text
                tab.graphic = tabLabel
            }
            textField.focusedProperty().onChange {
                if (!it) {
                    pageViewModel.pageName.value = textField.text
                    tab.graphic = tabLabel
                }
            }
        }
    }

}