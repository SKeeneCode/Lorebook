package org.ksoftware.lorebook.richtext

import java.text.BreakIterator
import java.util.ArrayList
import java.util.function.BiConsumer
import java.util.function.BiFunction

import org.fxmisc.richtext.GenericStyledArea
import org.fxmisc.richtext.Selection.Direction

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.text.Font
import javafx.scene.text.TextFlow
import org.fxmisc.richtext.NavigationActions.*
import org.fxmisc.richtext.StyledTextArea
import org.fxmisc.richtext.TextExt
import org.fxmisc.richtext.model.*


class StyledSegmentTextArea(
    val initialParStyle: String = "",
    val applyParStyle: BiConsumer<TextFlow, String> = BiConsumer { txtflow, pstyle -> txtflow.styleClass.add(pstyle) },
    val initialSegStyle: TextStyle = TextStyle.EMPTY
        .updateFontFamily(Font.getDefault().family)
        .updateFontName(Font.getDefault().name)
        .updateFontSize(Font.getDefault().size),
    val preserveStyle: Boolean = true,
    val segmentOps: TextOps<AbstractSegment, TextStyle> = MySegmentOps(),
    nodeFactory: (StyledSegment<AbstractSegment, TextStyle>) -> Node
) : GenericStyledArea<String, AbstractSegment, TextStyle>(
    initialParStyle,
    applyParStyle,
    initialSegStyle,
    segmentOps,
    preserveStyle,
    nodeFactory
) {

    var indent = false
    private val INDENT_DOC = ReadOnlyStyledDocument.fromSegment(IndentSegment("\t"), initialParStyle, initialSegStyle, segmentOps)
    private val INDENT_SEG = INDENT_DOC.getParagraph(0).styledSegments[0]

    constructor() : this(nodeFactory = { styledSegment -> styledSegment.segment.createNode(styledSegment.style.toCss()) })

    init {
        setStyleCodecs(Codec.STRING_CODEC, MySegmentCodec()) // Needed for copy paste.
        isWrapText = true

//         Intercept Enter to insert an indent after an empty line.
        addEventHandler(KeyEvent.KEY_PRESSED) { KE: KeyEvent ->
            if (indent && KE.code == KeyCode.ENTER) {
                val caretPosition = caretPosition
                if (getParagraph(currentParagraph - 1).length() == 0) {
                    Platform.runLater { replace(caretPosition, caretPosition, INDENT_DOC) }
                }
            }
        }

        // Hijack Ctrl+Left (incl Shift) to navigate around an indent.
        addEventFilter(KeyEvent.KEY_PRESSED) { KE ->
            if (KE.isShortcutDown) when (KE.code) {
                KeyCode.LEFT, KeyCode.KP_LEFT -> {
                    skipToPrevWord(KE.isShiftDown)
                    KE.consume()
                }
                else -> return@addEventFilter
            }
        }

        // Prevent the caret from appearing on the left hand side of an indent.
        caretPositionProperty().addListener { _, _, newPos ->
            if (indent && caretColumn == 0) {
                val seg = getParagraph(currentParagraph).segments[0]
                if (seg is IndentSegment) {
                    displaceCaret(newPos + 1)
                }
            }
        }
    }

    fun append(customSegment: AbstractSegment) {
        insert(length, customSegment)
    }

    fun insert(pos: Int, customSegment: AbstractSegment) {
        insert(pos, ReadOnlyStyledDocument.fromSegment(customSegment, initialParStyle, initialSegStyle, segmentOps))
    }



    override fun replace(start: Int, end: Int, replacement: StyledDocument<String, AbstractSegment, TextStyle>) {
        if (!indent) super.replace(start, end, replacement) else {
            val pl = replacement.paragraphs
            val db = ReadOnlyStyledDocumentBuilder(segmentOps, initialParStyle)
            for (p in pl.indices) {
                var segments = pl[p].styledSegments
                if (p > 1 && pl[p - 1].length() == 0) {
                    if (pl[p].segments[0] !is IndentSegment) {
                        if (segments is List<*>) {
                            segments = ArrayList(segments)
                        }
                        segments.add(0, INDENT_SEG)
                    }
                }
                db.addParagraph(segments)
            }
            super.replace(start, end, db.build())
        }
    }

    // Navigating around/over indents
    override fun nextChar(policy: SelectionPolicy) {
        if (caretPosition < length) {
            // offsetByCodePoints throws an IndexOutOfBoundsException unless colPos is adjusted to accommodate any indents, see this.moveTo
            moveTo(Direction.RIGHT, policy) { paragraphText: String, colPos: Int ->
                Character.offsetByCodePoints(paragraphText, colPos, + 1)
            }
        }
    }

    // Navigating around/over indents
    override fun previousChar(policy: SelectionPolicy) {
        if (caretPosition > 0) {
            // offsetByCodePoints throws an IndexOutOfBoundsException unless colPos is adjusted to accommodate any indents, see this.moveTo
            moveTo(Direction.LEFT, policy) { paragraphText: String, colPos: Int ->
                Character.offsetByCodePoints(paragraphText, colPos, - 1)
            }
        }
    }

    // Handles Ctrl+Left and Ctrl+Shift+Left
    private fun skipToPrevWord(isShiftDown: Boolean) {
        val caretPos = caretPosition
        if (caretPos >= 1) {
            var prevCharIsWhiteSpace = false
            if (indent && caretColumn == 1) {
                // Check for indent as charAt(0) throws an IndexOutOfBoundsException because Indents aren't represented by a character
                val seg = getParagraph(currentParagraph).segments[0]
                prevCharIsWhiteSpace = seg is IndentSegment
            }
            if (!prevCharIsWhiteSpace) prevCharIsWhiteSpace = Character.isWhitespace(getText(caretPos - 1, caretPos)[0])
            wordBreaksBackwards(
                if (prevCharIsWhiteSpace) 2 else 1,
                if (isShiftDown) SelectionPolicy.ADJUST else SelectionPolicy.CLEAR
            )
        }
    }

    /**
     * Skips n number of word boundaries backwards.
     */
    // Accommodating Indent
    override fun wordBreaksBackwards(n: Int, selection: SelectionPolicy) {
        if (length == 0) return
        moveTo(Direction.LEFT, selection) { paragraphText: String, colPos: Int ->
            val wordIterator = BreakIterator.getWordInstance()
            wordIterator.setText(paragraphText)
            wordIterator.preceding(colPos)
            for (i in 1 until n) {
                wordIterator.previous()
            }
            wordIterator.current()
        }
    }

    /**
     * Skips n number of word boundaries forward.
     */
    // Accommodating Indent
    override fun wordBreaksForwards(n: Int, selection: SelectionPolicy) {
        if (length == 0) return
        moveTo(Direction.RIGHT, selection) { paragraphText: String, colPos: Int ->
            val wordIterator = BreakIterator.getWordInstance()
            wordIterator.setText(paragraphText)
            wordIterator.following(colPos)
            for (i in 1 until n) {
                wordIterator.next()
            }
            wordIterator.current()
        }
    }

    /**
     * Because Indents are not represented in the text by a character there is a discrepancy
     * between the caret position and the text position which has to be taken into account.
     * So this method ADJUSTS the caret position before invoking the supplied function.
     *
     * @param dir LEFT for backwards, and RIGHT for forwards
     * @param selection CLEAR or ADJUST
     * @param colPosCalculator a function that receives PARAGRAPH text and an ADJUSTED
     * starting column position as parameters and returns an end column position.
     */
    private fun moveTo(dir: Direction, selection: SelectionPolicy, colPosCalculator: BiFunction<String, Int, Int>) {
        var colPos = caretColumn
        var pNdx = currentParagraph
        var p = getParagraph(pNdx)
        val pLen = p.length()
        var adjustCol = indent && p.segments[0] is IndentSegment
        if (adjustCol) colPos--
        if (dir === Direction.LEFT && colPos == 0) {
            p = getParagraph(--pNdx)
            adjustCol = indent && p.segments[0] is IndentSegment
            colPos = p.text.length // don't simplify !
        } else if (dir === Direction.RIGHT && (pLen == 0 || colPos > pLen - 1)) {
            p = getParagraph(++pNdx)
            adjustCol = indent && p.segments[0] is IndentSegment
            colPos = 0
        } else colPos = colPosCalculator.apply(p.text, colPos)
        if (adjustCol) colPos++
        moveTo(pNdx, colPos, selection)
    }
}