package org.ksoftware.lorebook

import javafx.scene.Node
import javafx.scene.text.TextFlow
import org.fxmisc.richtext.GenericStyledArea
import org.fxmisc.richtext.StyledTextArea
import org.fxmisc.richtext.TextExt
import org.fxmisc.richtext.model.SegmentOps
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyledSegment
import org.fxmisc.richtext.model.TextOps
import org.ksoftware.lorebook.richtext.ParStyle
import org.ksoftware.lorebook.richtext.TextStyle
import tornadofx.*
import java.util.function.BiConsumer
import java.util.function.Function

class MyView : View("My View") {

    private val styledTextOps: TextOps<String, TextStyle> = SegmentOps.styledTextOps()

    private val area = GenericStyledArea(
            ParStyle.EMPTY,                                                                                  // default paragraph style
            { paragraph: TextFlow, style: ParStyle -> paragraph.style = style.toCss() },                     // paragraph style setter
            TextStyle.EMPTY,                                                                                 // default segment style
            styledTextOps,                                                                                   // segment operations
            { seg -> createNode(seg) { text: TextExt, style: TextStyle -> text.style = style.toCss() } })


    override val root = vbox {
        hbox {
            button("B") {
                action {
                    toggleBold()
                }
            }
            button("I") {
                action {
                    toggleItalic()
                }
            }
        }
        area.isWrapText = true
        add(area)
    }

    private fun createNode(seg: StyledSegment<String, TextStyle>, applyStyle: BiConsumer<TextExt, TextStyle>): Node {
        return StyledTextArea.createStyledTextNode(seg.segment, seg.style, applyStyle) }

    private fun updateStyleInSelection(mixinGetter: Function<StyleSpans<TextStyle>, TextStyle>) {
        val selection = area.selection
        if (selection.length != 0) {
            val styles = area.getStyleSpans(selection)
            val mixin = mixinGetter.apply(styles)
            val newStyles = styles.mapStyles { style: TextStyle -> style.updateWith(mixin) }
            area.setStyleSpans(selection.start, newStyles)
        }
    }

    private fun updateStyleInSelection(mixin: TextStyle) {
        val selection = area.selection
        if (selection.length != 0) {
            val styles = area.getStyleSpans(selection)
            val newStyles = styles.mapStyles{ style: TextStyle -> style.updateWith(mixin) }
            area.setStyleSpans(selection.start, newStyles)
        }
    }

    private fun toggleBold() = updateStyleInSelection { spans: StyleSpans<TextStyle> -> TextStyle.bold(!spans.styleStream().allMatch { style: TextStyle -> style.bold.orElse(false) }) }
    private fun toggleItalic() = updateStyleInSelection { spans: StyleSpans<TextStyle> -> TextStyle.italic(!spans.styleStream().allMatch { style: TextStyle -> style.italic.orElse(false) }) }


    }

