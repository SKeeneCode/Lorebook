package org.ksoftware.lorebook.controls

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.PopupWindow
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
    private val entriesPopup: ContextMenu = ContextMenu()

    override val root = textfield {
        promptText = "Add page tag..."
        minWidth = 100.0
        prefWidth = 100.0
    }

    init {
        entriesPopup.anchorLocation = PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT
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
                        entriesPopup.show(root, Side.BOTTOM, 0.0, root.layoutY - 5) //position of popup
                        entriesPopup.requestFocus()
                    }
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
        val menuItems: MutableList<CustomMenuItem> = LinkedList()
        //List size - 10 or founded suggestions count
        val maxEntries = 8
        val count = searchResult.size.coerceAtMost(maxEntries)
        //Build list as set of labels
        for (i in 0 until count) {
            val result = searchResult[i]
            //label with graphic (text flow) to highlight founded subtext in suggestions
            val entryLabel = Label()
            val itemGraphic = hbox {
                alignment = Pos.CENTER
                style {
                    backgroundColor += result.colorProperty.value
                    backgroundRadius = multi(box(12.px))
                }
                paddingHorizontal = 16
                paddingVertical = 8
                add(buildTextFlow(result, searchRequest))
            }
           // entryLabel.prefHeight = 10.0 //don't sure why it's changed with "graphic"
            val item = CustomMenuItem(itemGraphic, true)
            menuItems.add(item)

            //if any suggestion is select set it into text and close popup
            item.onAction = EventHandler {
                pageTags.add(result)
                root.clear()
                entriesPopup.hide()
            }
        }

        //"Refresh" context menu
        entriesPopup.items.clear()
        entriesPopup.items.addAll(menuItems)
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