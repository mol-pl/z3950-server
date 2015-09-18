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
 * Pobiera liczbę wyników i konkretne wyniki z kontrolera tenanta.
 *
 * @author Paweł
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
	 * Domyślny tylko na wypadek błędu, bo searchable musi coś zwrócić.
	 */
	public MolNetResultSet() {
	}

	/**
	 * Konstruktor
	 * 
	 * @param params parametry zapytania
	 * @throws Exception nie znaleziono bazy
	 */
	public MolNetResultSet(HttpQueryParams params) throws Exception {
		this.httpQueryParams = params;
		this.httpDataProvider = new HttpDataProvider();

		try {
			//wyślij req o count
			this.num_hits = httpDataProvider.getCount(httpQueryParams);

			//tworzy pusty rekord, który będzie wstawiany gdy wystąpił problemy z pobraniem rekordu
			prepareDummyRecord();
		} catch (Exception ex) {
			throw new Exception("Database not found");
		}

		log.debug("num_hits=" + num_hits);
	}

	/**
	 * Tworzy pusty rekord, za wypadek gdyby serwer zwrócił łędny rekord
	 * baz tego sypie się napełnianie cache i serwer się zapętla
	 * 
	 * @throws Exception nie udało się sparsować przykładowego rekordu
	 */
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

	/**
	 * Tworzy obiekt informacji o rekordzie
	 * 
	 * @param hit_no numer rekordu w zbiorze (potrzeban dla cache)
	 * @param data rekord w formacie iso2709
	 * @return obiekt informacji
	 */
	private InformationFragment prepareInformationFragment(int hit_no, byte[] data) {
		return new org.jzkit.search.util.RecordModel.InformationFragmentImpl(hit_no,
				"REPO",
				"COLL",
				null,
				data, new ExplicitRecordFormatSpecification("iso2709", "usmarc", "F"));
	}

	/**
	 * Pobiera rekordy w postaci obiektów informacji o rekordach
	 * 
	 * @param starting_fragment numer pierwszego obiektu
	 * @param count liczba obiektów
	 * @param spec zalecany format rekordu
	 * @return InformationFragment[]
	 * @throws IRResultSetException wyszukwiwanie nieudane, zastąpione zwracaniem pustej tablicy
	 * bo się zapętlał cache
	 */
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
