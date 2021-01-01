package org.ksoftware.lorebook.test

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import java.util.*
import java.util.stream.Collectors


class AutocompletionTextField : TextField() {
    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    //Local variables
    //entries to autocomplete
    val entries: SortedSet<String>

    //popup GUI
    private val entriesPopup: ContextMenu

    /**
     * "Suggestion" specific listners
     */
    private fun setListner() {
        //Add "suggestions" by changing text
        textProperty().addListener { observable, oldValue, newValue ->
            val enteredText: String = getText()
            //always hide suggestion if nothing has been entered (only "spacebars" are disallowed in TextFieldWithLengthLimit)
            if (enteredText.isEmpty()) {
                entriesPopup.hide()
            } else {
                //filter all possible suggestions depends on "Text", case insensitive
                val filteredEntries = entries.stream()
                        .filter { e: String -> e.toLowerCase().contains(enteredText.toLowerCase()) }
                        .collect(Collectors.toList())
                //some suggestions are found
                if (!filteredEntries.isEmpty()) {
                    //build popup - list of "CustomMenuItem"
                    populatePopup(filteredEntries, enteredText)
                    if (!entriesPopup.isShowing) { //optional
                        entriesPopup.show(this@AutocompletionTextField, Side.BOTTOM, 0.0, 0.0) //position of popup
                    }
                    //no suggestions -> hide
                } else {
                    entriesPopup.hide()
                }
            }
        }

        //Hide always by focus-in (optional) and out
        focusedProperty().addListener { observableValue, oldValue, newValue -> entriesPopup.hide() }
    }

    /**
     * Populate the entry set with the given search results. Display is limited to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private fun populatePopup(searchResult: List<String>, searchReauest: String) {

        //List of "suggestions"
        val menuItems: MutableList<CustomMenuItem> = LinkedList()
        //List size - 10 or founded suggestions count
        val maxEntries = 10
        val count = Math.min(searchResult.size, maxEntries)
        //Build list as set of labels
        for (i in 0 until count) {
            val result = searchResult[i]
            //label with graphic (text flow) to highlight founded subtext in suggestions
            val entryLabel = Label()
            entryLabel.setGraphic(buildTextFlow(result, searchReauest))
            entryLabel.setPrefHeight(10.0) //don't sure why it's changed with "graphic"
            val item = CustomMenuItem(entryLabel, true)
            menuItems.add(item)

            //if any suggestion is select set it into text and close popup
            item.onAction = EventHandler { actionEvent: ActionEvent? ->
                setText(result)
                positionCaret(result.length)
                entriesPopup.hide()
            }
        }

        //"Refresh" context menu
        entriesPopup.items.clear()
        entriesPopup.items.addAll(menuItems)
    }
    fun buildTextFlow(text: String, filter: String): TextFlow? {
        val filterIndex = text.toLowerCase().indexOf(filter.toLowerCase())
        val textBefore = Text(text.substring(0, filterIndex))
        val textAfter = Text(text.substring(filterIndex + filter.length))
        val textFilter = Text(text.substring(filterIndex, filterIndex + filter.length)) //instead of "filter" to keep all "case sensitive"
        textFilter.setFill(Color.ORANGE)
        textFilter.setFont(Font.font("Helvetica", FontWeight.BOLD, 12.0))
        return TextFlow(textBefore, textFilter, textAfter)
    }

    init {
        entries = TreeSet()
        entriesPopup = ContextMenu()
        setListner()
    }
}