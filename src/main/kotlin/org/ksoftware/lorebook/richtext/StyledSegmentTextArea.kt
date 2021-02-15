package org.ksoftware.lorebook.richtext

import javafx.scene.Node
import javafx.scene.text.Font
import javafx.scene.text.TextFlow
import org.fxmisc.richtext.GenericStyledArea
import org.fxmisc.richtext.model.ReadOnlyStyledDocument
import org.fxmisc.richtext.model.StyledSegment
import org.fxmisc.richtext.model.TextOps
import java.util.function.BiConsumer


class StyledSegmentTextArea(
    initialParStyle: ParStyle = ParStyle.EMPTY.updateAlignment(TextAlignment.LEFT),
    applyParStyle: BiConsumer<TextFlow, ParStyle> = BiConsumer { txtflow, pstyle -> txtflow.style = pstyle.toCss() },
    initialSegStyle: TextStyle = TextStyle.EMPTY
        .updateFontFamily(Font.getDefault().family)
        .updateFontName(Font.getDefault().name)
        .updateFontSize(Font.getDefault().size),
    preserveStyle: Boolean = true,
    segmentOps: TextOps<AbstractSegment, TextStyle> = MySegmentOps(),
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
        insert(pos, ReadOnlyStyledDocument.fromSegment(customSegment, initialParagraphStyle, initialTextStyle, segOps))
    }

}