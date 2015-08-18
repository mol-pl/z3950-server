package pl.mol.molnet.z3950.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.*;
import org.apache.xml.serialize.XMLSerializer;
import org.marc4j.*;
import org.w3c.dom.Document;
import org.jzkit.search.util.RecordBuilder.RecordBuilderException;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

/**
 * Nadpisuje org.jzkit.search.util.RecordBuilder.Iso2709Utf8RecordFactory
 dodaje obsługę utf-8
 *
 * @author Pawel
 */
public class Iso2709Utf8RecordFactory extends org.jzkit.search.util.RecordBuilder.iso2709RecordFactory {

	private static Log log = LogFactory.getLog(Iso2709Utf8RecordFactory.class);

	/**
	 * Tworzy dokument binarny z marcxml
	 *
	 * @param input_dom marcxml
	 * @param esn Optional Element Set Name (F,B, For XML, a schema to request);
	 * @return native_object
	 * @throws org.jzkit.search.util.RecordBuilder.RecordBuilderException
	 */
	@Override
	public Object createFrom(Document input_dom, String esn) throws RecordBuilderException {
		byte[] result = null;
		log.debug("iso2709 from marcxml");
		try {
			log.debug("open pip output stream");
			PipedOutputStream pos = new PipedOutputStream();
			XMLSerializer serializer = new XMLSerializer();
			MarcXmlReader reader = new MarcXmlReader(new PipedInputStream(pos));
			serializer.setOutputByteStream(pos);
			log.debug("Serialize dom to output stream");
			serializer.serialize(input_dom);
			log.debug("flush");
			pos.flush();
			pos.close();
			log.debug("attempt to read marcxml from pipe");
			if (reader.hasNext()) {
				Record record = reader.next();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				MarcWriter writer = new MarcStreamWriter(baos, "UTF8");
				writer.write(record);
				result = baos.toByteArray();
				log.debug("Result of transform to marc: " + new String(result));
			} else {
				log.warn("No marc record found in reader stream");
			}
		} catch (java.io.IOException ioe) {
			throw new RecordBuilderException("Problem converting marcxml to iso2709", ioe);
		}

		log.debug("result=" + result);
		return result;
	}

	/**
	 * Tworzy marcxml z obiektu binarnego
	 *
	 * @param native_object dokument binarny
	 * @return marcxml
	 * @throws RecordBuilderException
	 */
	@Override
	public Document getCanonicalXML(Object native_object) throws RecordBuilderException {
		org.w3c.dom.Document retval = null;
		try {
			MarcReader reader = new MarcStreamReader(new ByteArrayInputStream((byte[]) native_object), "UTF8");
			javax.xml.transform.dom.DOMResult result = new javax.xml.transform.dom.DOMResult();
			MarcXmlWriter writer = new MarcXmlWriter(result);
			if (reader.hasNext()) {
				Record record = (Record) reader.next();
				writer.write(record);
			} else {
				log.warn("No marc record found in reader stream");
			}
			writer.close();

			retval = (Document) result.getNode();
		} catch (Exception e) {
			throw new RecordBuilderException("Problem creating marcxml from iso2709", e);
		}
		return retval;
	}
}
