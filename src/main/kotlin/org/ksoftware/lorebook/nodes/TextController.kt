package org.ksoftware.lorebook.nodes

import org.fxmisc.richtext.model.TwoDimensional
import org.ksoftware.lorebook.richtext.ParStyle
import org.ksoftware.lorebook.richtext.StyledSegmentTextArea
import org.ksoftware.lorebook.richtext.TextStyle
import tornadofx.Controller

/**
 * The text controller is responsible for updating the style of segments in a given StyledSegmentTextArea
 */
class TextController : Controller() {


    /**
     * Updates the selection in a given StyledSegmentTextArea with the provided Style
     */
     fun updateStyleInSelection(area: StyledSegmentTextArea, mixin: TextStyle) {
        val selection = area.selection
        if (selection.length != 0) {
            val styles = area.getStyleSpans(selection)
            val newStyles = styles.mapStyles{ style: TextStyle -> style.updateWith(mixin) }
            area.setStyleSpans(selection.start, newStyles)
        }
    }

    /**
     * Updates all paragraphs in the selection of a given StyledSegmentTextArea. Each paragraph style is updated using
     * the provided updater function.
     */
    fun updateParagraphStyleInSelection(area: StyledSegmentTextArea, updater: (ParStyle) -> ParStyle) {
        val selection = area.selection
        val startPar = area.offsetToPosition(selection.start, TwoDimensional.Bias.Forward).major
        val endPar = area.offsetToPosition(selection.end, TwoDimensional.Bias.Backward).major
        for (i in startPar..endPar) {
            val paragraph = area.getParagraph(i)
            area.setParagraphStyle(i, updater(paragraph.paragraphStyle))
        }
    }

}