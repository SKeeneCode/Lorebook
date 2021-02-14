package org.ksoftware.lorebook.richtext

import java.util.function.BiConsumer
import org.fxmisc.richtext.GenericStyledArea
import javafx.scene.Node
import javafx.scene.text.Font
import javafx.scene.text.TextFlow
import org.fxmisc.richtext.model.*


class StyledSegmentTextArea(
    val initialParStyle: ParStyle = ParStyle.EMPTY.updateAlignment(TextAlignment.JUSTIFY),
    val applyParStyle: BiConsumer<TextFlow, ParStyle> = BiConsumer { txtflow, pstyle -> txtflow.style = pstyle.toCss() },
    var initialSegStyle: TextStyle = TextStyle.EMPTY
        .updateFontFamily(Font.getDefault().family)
        .updateFontName(Font.getDefault().name)
        .updateFontSize(Font.getDefault().size),
    val preserveStyle: Boolean = true,
    val segmentOps: TextOps<AbstractSegment, TextStyle> = MySegmentOps(),
    nodeFactory: (StyledSegment<AbstractSegment, TextStyle>) -> Node
) : GenericStyledArea<ParStyle, AbstractSegment, TextStyle>(
    initialParStyle,
    applyParStyle,
    initialSegStyle,
    segmentOps,
    preserveStyle,
    nodeFactory
) {

    constructor() : this(nodeFactory = { styledSegment -> styledSegment.segment.createNode(styledSegment.style.toCss()) })

    init {
        setStyleCodecs(ParStyle.CODEC, MySegmentCodec()) // Needed for copy paste.
        paragraphGraphicFactory = BulletFactory(this)
        isWrapText = true
    }

    fun append(customSegment: AbstractSegment) {
        insert(length, customSegment)
    }

    fun insert(pos: Int, customSegment: AbstractSegment) {
        insert(pos, ReadOnlyStyledDocument.fromSegment(customSegment, initialParStyle, initialSegStyle, segmentOps))
    }

}