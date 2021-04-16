package gbas.gtbch.util;

import org.xml.sax.InputSource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class XmlFormatter {

    private XmlFormatter() {
    }

    public static String formatXml(String xml) {

        try {
            final XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(xml.getBytes()));
            String encoding = xmlStreamReader.getCharacterEncodingScheme();
            if (encoding == null) {
                encoding = StandardCharsets.UTF_8.name();
            }

            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();

            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            Source xmlSource = new SAXSource(new InputSource(
                    new ByteArrayInputStream(xml.getBytes(Charset.forName(encoding)))));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());

            serializer.transform(xmlSource, res);

            xml = new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray(), Charset.forName(encoding));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return xml;
    }

}
