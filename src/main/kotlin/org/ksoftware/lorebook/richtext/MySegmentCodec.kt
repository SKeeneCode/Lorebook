package org.ksoftware.lorebook.richtext

import org.fxmisc.richtext.model.Codec
import org.fxmisc.richtext.model.StyledSegment
import java.io.*
import javax.xml.bind.JAXB


class MySegmentCodec : Codec<StyledSegment<AbstractSegment, String>> {
    override fun getName(): String {
        return "AbstractSegment"
    }

    override fun encode(os: DataOutputStream, styledSeg: StyledSegment<AbstractSegment, String>) {
        val seg = styledSeg.segment

        os.writeUTF(seg.javaClass.name)
        os.writeUTF(seg.data!!.javaClass.name)

        val data2xml: Writer = StringWriter()
        JAXB.marshal(seg.data, data2xml)
        os.writeUTF(data2xml.toString())

        var style = styledSeg.style
        if (style == null) style = ""
        os.writeUTF(style)
    }

    override fun decode(inputStream: DataInputStream): StyledSegment<AbstractSegment, String> {
        val segmentType = inputStream.readUTF()
        val dataType = inputStream.readUTF()
        val xmlData = inputStream.readUTF()
        val style = inputStream.readUTF()

            val xml2data: Reader = StringReader(xmlData)
            val dataClass = Class.forName(dataType)
            val data: Any = JAXB.unmarshal(xml2data, dataClass)
            val segClass = Class.forName(segmentType)
        println(segClass)
        println(TextSegment::class.java)
        return when (segClass) {
             TextSegment::class.java ->   StyledSegment(TextSegment(data as String), style)
             LabelSegment::class.java ->  StyledSegment(LabelSegment(data), style)
            else ->  StyledSegment(TextSegment(""), style)
        }
    }
}