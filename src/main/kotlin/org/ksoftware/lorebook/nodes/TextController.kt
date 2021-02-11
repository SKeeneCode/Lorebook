package org.ksoftware.lorebook.nodes

import org.fxmisc.richtext.model.StyleSpans
import org.ksoftware.lorebook.richtext.StyledSegmentTextArea
import org.ksoftware.lorebook.richtext.TextStyle
import tornadofx.Controller
import java.util.function.Function

class TextController : Controller() {

     fun updateStyleInSelection(area: StyledSegmentTextArea, mixinGetter: Function<StyleSpans<TextStyle>, TextStyle>) {
        val selection = area.selection
        if (selection.length != 0) {
            val styles = area.getStyleSpans(selection)
            val mixin = mixinGetter.apply(styles)
            val newStyles = styles.mapStyles { style: TextStyle -> style.updateWith(mixin) }
            area.setStyleSpans(selection.start, newStyles)
        }
    }

     fun updateStyleInSelection(area: StyledSegmentTextArea, mixin: TextStyle) {
        val selection = area.selection
        if (selection.length != 0) {
            val styles = area.getStyleSpans(selection)
            val newStyles = styles.mapStyles{ style: TextStyle -> style.updateWith(mixin) }
            area.setStyleSpans(selection.start, newStyles)
        }
    }

    fun toggleBold(area: StyledSegmentTextArea) = updateStyleInSelection(area) { spans: StyleSpans<TextStyle> -> TextStyle.bold(!spans.styleStream().allMatch { style: TextStyle -> style.bold.orElse(false) }) }
    fun toggleItalic(area: StyledSegmentTextArea) = updateStyleInSelection(area) { spans: StyleSpans<TextStyle> -> TextStyle.italic(!spans.styleStream().allMatch { style: TextStyle -> style.italic.orElse(false) }) }

}