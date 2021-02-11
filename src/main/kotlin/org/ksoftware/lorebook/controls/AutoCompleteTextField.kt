package org.ksoftware.lorebook.controls

import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ListView
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.Popup
import javafx.stage.PopupWindow
import org.fxmisc.wellbehaved.event.EventPattern
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.Nodes
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.utilities.getContrastColor
import tornadofx.View
import tornadofx.*
import tornadofx.textfield
import java.util.*
import java.util.stream.Collectors

class AutoCompleteTextField : View()  {

    private val pageViewModel: PageViewModel by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val pageTags = pageViewModel.tags.value
    private val entriesPopup = Popup()
    private val entriesList = ListView<SearchResult>()

    override val root = textfield {
        promptText = "Add page tag..."
        minWidth = 100.0
        prefWidth = 100.0
    }

    init {
        entriesPopup.anchorLocation = PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT

        entriesList.apply {
            prefWidth = 150.0
            cellFormat {
                graphic = it.graphic()
                onLeftClick {
                    pageTags.add(it.result)
                    root.clear()
                    entriesPopup.hide()
                }
            }
        }

        entriesList.prefHeightProperty().bind(Bindings.size(entriesList.items).multiply(31));

        Nodes.addInputMap(entriesList, InputMap.sequence(
            InputMap.consume(EventPattern.keyPressed(KeyCode.ENTER)) {
                if (entriesPopup.isShowing && entriesList.selectionModel.selectedItem != null) {
                    pageTags.add(entriesList.selectionModel.selectedItem.result)
                    root.clear()
                    entriesPopup.hide()
                }
            },
            InputMap.consume(EventPattern.keyPressed(KeyCode.LEFT)) {
                root.positionCaret(root.caretPosition - 1)
            },
            InputMap.consume(EventPattern.keyPressed(KeyCode.RIGHT)) {
                root.positionCaret(root.caretPosition + 1)
            }
        ))

        entriesPopup.content.add(entriesList)

        setListener()
    }

    private fun setListener() {
        //Add "suggestions" by changing text
        root.textProperty().addListener { _, _, _ ->
            val enteredText: String = root.text
            //always hide suggestion if nothing has been entered (only "spacebars" are disallowed in TextFieldWithLengthLimit)
            if (enteredText.isEmpty()) {
                entriesPopup.hide()
            } else {
                //filter all possible suggestions depends on "Text", case insensitive
                val filteredEntries = projectViewModel.rootTag.allDescendants().stream()
                    .filter { e -> e.nameProperty.value.toLowerCase().contains(enteredText.toLowerCase()) && ( e !in pageTags) }
                    .collect(Collectors.toList())
                //some suggestions are found
                if (filteredEntries.isNotEmpty()) {
                    //build popup - list of "CustomMenuItem"
                    populatePopup(filteredEntries, enteredText)
                    if (!entriesPopup.isShowing) { //optional
                        val anchorPoint: Point2D = root.localToScreen(0.0, 0.0)
                        entriesPopup.show(root, anchorPoint.x, anchorPoint.y) //position of popup
                    }
                    entriesPopup.requestFocus()
                    entriesList.selectionModel.select(0)
                    entriesList.focusModel.focus(0)
                    //no suggestions -> hide
                } else {
                    entriesPopup.hide()
                }
            }
        }

        //Hide always by focus-in (optional) and out
        root.focusedProperty().addListener { _, _, _ -> entriesPopup.hide() }

    }

    /**
     * Populate the entry set with the given search results. Display is limited to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private fun populatePopup(searchResult: List<TagModel>, searchRequest: String) {
        //List of "suggestions"
        val menuItems: MutableList<SearchResult> = LinkedList()
        //List size - 10 or founded suggestions count
        val maxEntries = 8
        val count = searchResult.size.coerceAtMost(maxEntries)
        //Build list as set of labels
        for (i in 0 until count) {
            val result = SearchResult(searchResult[i], searchRequest)
            menuItems.add(result)
        }

        //"Refresh" context menu
        entriesList.items.clear()
        entriesList.items.addAll(menuItems)
    }


    private class SearchResult(val result: TagModel, private val searchString: String) {
        fun graphic() : Node {
            return HBox().apply {
            //label with graphic (text flow) to highlight founded subtext in suggestions
                alignment = Pos.CENTER
                style {
                    backgroundColor += result.colorProperty.value
                    backgroundRadius = multi(box(12.px))
                }
                paddingHorizontal = 16
                paddingVertical = 8
                add(buildTextFlow(result, searchString))
            }
        }

        private fun buildTextFlow(tag: TagModel, filter: String): TextFlow {
            val text = tag.nameProperty.value
            val filterIndex = text.toLowerCase().indexOf(filter.toLowerCase())
            val textBefore = Text(text.substring(0, filterIndex))
            textBefore.fill = getContrastColor(tag.colorProperty.value)
            val textAfter = Text(text.substring(filterIndex + filter.length))
            textAfter.fill = getContrastColor(tag.colorProperty.value)
            val textFilter = Text(text.substring(filterIndex, filterIndex + filter.length)) //instead of "filter" to keep all "case sensitive"
            textFilter.fill = Color.ORANGE
            textFilter.font = Font.font("Helvetica", FontWeight.BOLD, 12.0)
            return TextFlow(textBefore, textFilter, textAfter)
        }
    }


}