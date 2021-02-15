package org.ksoftware.lorebook.richtext

import jakarta.xml.bind.JAXB
import javafx.scene.text.Font
import org.fxmisc.richtext.model.Codec
import org.fxmisc.richtext.model.StyledSegment
import java.io.*


class MySegmentCodec : Codec<StyledSegment<AbstractSegment, TextStyle>> {
    override fun getName(): String {
        return "AbstractSegment"
    }

    override fun encode(os: DataOutputStream, styledSeg: StyledSegment<AbstractSegment, TextStyle>) {
        val seg = styledSeg.segment

        os.writeUTF(seg.javaClass.name)
        os.writeUTF(seg.data!!.javaClass.name)

        val data2xml: Writer = StringWriter()
        JAXB.marshal(seg.data, data2xml)
        os.writeUTF(data2xml.toString())

        var style = styledSeg.style
        if (style == null) style = TextStyle.EMPTY
        os.writeUTF(style.fontFamily.orElse(Font.getDefault().family))
        os.writeUTF(style.fontName.orElse(Font.getDefault().name))
        os.writeUTF(style.fontSize.orElse(Font.getDefault().size).toString())
        os.writeUTF(style.bold.orElse(false).toString())
        os.writeUTF(style.italic.orElse(false).toString())
    }

    override fun decode(inputStream: DataInputStream): StyledSegment<AbstractSegment, TextStyle> {
        val segmentType = inputStream.readUTF()
        val dataType = inputStream.readUTF()
        val xmlData = inputStream.readUTF()
        val family = inputStream.readUTF()
        val name = inputStream.readUTF()
        val size = inputStream.readUTF().toDouble()
        val bold = inputStream.readUTF().toBoolean()
        val italic = inputStream.readUTF().toBoolean()

        val xml2data: Reader = StringReader(xmlData)
        val dataClass = Class.forName(dataType)
        val data: Any = JAXB.unmarshal(xml2data, dataClass)
        val segClass = Class.forName(segmentType)
        var textStyle = TextStyle.EMPTY
            .updateFontFamily(family)
            .updateFontName(name)
            .updateFontSize(size)
                if (bold) textStyle = textStyle.updateBold(bold)
                if (italic) textStyle = textStyle.updateItalic(italic)
        return when (segClass) {
            TextSegment::class.java -> {
                StyledSegment(TextSegment(data as String), textStyle)
            }
            LabelSegment::class.java -> StyledSegment(LabelSegment(data), textStyle)
            else -> StyledSegment(TextSegment(""), textStyle)
        }
    }
}