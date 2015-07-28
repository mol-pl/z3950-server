package pl.mol.molnet.z3950.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.util.StringInputStream;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

/**
 * Pobiera liczb� wynik�w i konkretne wyniki z kontrolera tenanta.
 *
 * @author Pawe�
 */
public class MolNetResultSet extends AbstractIRResultSet implements IRResultSet {

	private static Log log = LogFactory.getLog(MolNetResultSet.class);
	private int num_hits = 0;
	private HttpDataProvider httpDataProvider;
	private HttpQueryParams httpQueryParams;
	private byte[] dummyRecord;

	@Override
	public void setResultSetName(String name) {
		if (this.result_set_name == null) {
			this.result_set_name = name;
		}
	}

	/**
	 * Domy�lny tylko na wypadek b��du, bo searchable musi co� zwr�ci�.
	 */
	public MolNetResultSet() {
	}

	public MolNetResultSet(HttpQueryParams params) throws Exception {
		this.httpQueryParams = params;
		this.httpDataProvider = new HttpDataProvider();

		try {
			//wy�lij req o count
			this.num_hits = httpDataProvider.getCount(httpQueryParams);

			//tworzy pusty rekord, kt�ry b�dzie wstawiany gdy wyst�pi� problemy z pobraniem rekordu
			prepareDummyRecord();
		} catch (Exception ex) {
			throw new Exception("Database not found");
		}

		log.debug("num_hits=" + num_hits);
	}

	private void prepareDummyRecord() throws Exception {
		try {
			String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<marc:collection xmlns:marc=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">\n"
					+ "<marc:record>\n"
					+ "<marc:datafield tag=\"010\" ind1=\" \" ind2=\" \">\n"
					+ "<marc:subfield code=\"a\">CANNOT FETCH RECORD</marc:subfield>\n"
					+ "</marc:datafield>\n"
					+ "</marc:record>\n"
					+ "</marc:collection>";

			MarcXmlReader reader = new MarcXmlReader(new StringInputStream(doc));
			if (reader.hasNext()) {
				Record record = reader.next();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				MarcWriter writer = new MarcStreamWriter(baos);
				writer.write(record);
				dummyRecord = baos.toByteArray();
				writer.close();
				baos.close();
			}

		} catch (IOException ex) {
			throw new Exception("Parsing resultset failed");
		}
	}

	private InformationFragment prepareInformationFragment(int hit_no, byte[] data) {
		return new org.jzkit.search.util.RecordModel.InformationFragmentImpl(hit_no,
				"REPO",
				"COLL",
				null,
				data, new ExplicitRecordFormatSpecification("iso2709", "usmarc", "F"));
	}

	// Fragment Source methods
	@Override
	public InformationFragment[] getFragment(int starting_fragment,
			int count,
			RecordFormatSpecification spec) throws IRResultSetException {
		InformationFragment[] result = new InformationFragment[count];
		Record[] records;
		try {
			records = httpDataProvider.getList(httpQueryParams, starting_fragment - 1, count);
		} catch (Exception ex) {
			//throw new IRResultSetException("Search failed");
			return result;
		}

		for (int i = 0; i < records.length; i++) {
			try {
				if (records[i] != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					MarcWriter writer = new MarcStreamWriter(baos, "UTF8");
					writer.write(records[i]);     
					result[i] = prepareInformationFragment(starting_fragment + i, baos.toByteArray());
					writer.close();
					baos.close();
				} else {
					result[i] = prepareInformationFragment(starting_fragment + i, dummyRecord.clone());
				}
			} catch (IOException ex) {
				//throw new IRResultSetException("Parsing resultset failed");
				return result;
			}
		}

		return result;
	}

	@Override
	public void asyncGetFragment(int starting_fragment,
			int count,
			RecordFormatSpecification spec,
			IFSNotificationTarget target) {
		try {
			InformationFragment[] result = getFragment(starting_fragment, count, spec);
			target.notifyRecords(result);
		} catch (IRResultSetException irrse) {
			target.notifyError("bib-1", 0, "Problem obtaining result records", irrse);
		}
	}

	/**
	 * Current number of fragments available
	 *
	 * @return
	 */
	@Override
	public int getFragmentCount() {
		return num_hits;
	}

	/**
	 * The size of the result set (Estimated or known)
	 *
	 * @return
	 */
	@Override
	public int getRecordAvailableHWM() {
		return num_hits;
	}

	/**
	 * Release all resources and shut down the object
	 */
	@Override
	public void close() {
	}

	@Override
	public IRResultSetInfo getResultSetInfo() {
		return new IRResultSetInfo(getResultSetName(),
				getFragmentCount(),
				getStatus());
	}
}
