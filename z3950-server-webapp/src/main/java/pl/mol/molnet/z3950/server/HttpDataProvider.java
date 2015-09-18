package pl.mol.molnet.z3950.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;
import org.jzkit.search.util.QueryModel.Internal.ComplexNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelNamespaceNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.jzkit.search.util.QueryModel.Internal.QueryNode;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

/**
 * Dostarcza metody do komunikacji z kontrolerem w molnet
 *
 * @author Paweł
 */
public class HttpDataProvider {

	private final Pattern patternTitle = Pattern.compile("bib-1\\.1\\.4");
	private final Pattern patternAuthor = Pattern.compile("bib-1\\.1\\.1003");
	private final Pattern patternIsbn = Pattern.compile("bib-1\\.1\\.7");
	private final Pattern patternPublDate = Pattern.compile("bib-1\\.1\\.31");
	private final HttpClient client = new HttpClient();

	private String attrset;

	/**
	 * Tworzy parametry zapytania get
	 * 
	 * @param params kryteria zapytania
	 * @param start początek pobierania rekordów
	 * @param limit liczba pobranych rekordów
	 * @return parametry get
	 * @throws UnsupportedEncodingException nie ma wskazanego kodowania (utf-8)
	 */
	private String prepareGetParams(HttpQueryParams params, Integer start, Integer limit) throws UnsupportedEncodingException {
		String urlParameters = null;

		if (params.getAuthor() != null) {
			urlParameters = "author=" + URLEncoder.encode(params.getAuthor(), "UTF-8");
		}

		if (params.getTitle() != null) {
			if (urlParameters != null) {
				urlParameters = urlParameters + "&title=" + URLEncoder.encode(params.getTitle(), "UTF-8");
			} else {
				urlParameters = "title=" + URLEncoder.encode(params.getTitle(), "UTF-8");
			}
		}

		if (params.getIsbn() != null) {
			if (urlParameters != null) {
				urlParameters = urlParameters + "&isbn=" + URLEncoder.encode(params.getIsbn(), "UTF-8");
			} else {
				urlParameters = "isbn=" + URLEncoder.encode(params.getIsbn(), "UTF-8");
			}
		}

		if (params.getPublDate() != null) {
			if (urlParameters != null) {
				urlParameters = urlParameters + "&date=" + URLEncoder.encode(params.getPublDate(), "UTF-8");
			} else {
				urlParameters = "date=" + URLEncoder.encode(params.getPublDate(), "UTF-8");
			}
		}

		if (params.getBibliographicDbAlias() != null) {
			if (urlParameters != null) {
				urlParameters = urlParameters + "&bibdbalias=" + URLEncoder.encode(params.getBibliographicDbAlias(), "UTF-8");
			} else {
				urlParameters = "bibdbalias=" + URLEncoder.encode(params.getBibliographicDbAlias(), "UTF-8");
			}
		}

		if (start != null) {
			if (urlParameters != null) {
				urlParameters = urlParameters + "&start=" + URLEncoder.encode(start.toString(), "UTF-8");
			} else {
				urlParameters = "start=" + URLEncoder.encode(start.toString(), "UTF-8");
			}
		}

		if (limit != null) {
			if (urlParameters != null) {
				urlParameters = urlParameters + "&limit=" + URLEncoder.encode(limit.toString(), "UTF-8");
			} else {
				urlParameters = "limit=" + URLEncoder.encode(limit.toString(), "UTF-8");
			}
		}

		return urlParameters;
	}

	/**
	 * Pobiera liczbę rekordów z bazy tenenta
	 * 
	 * @param params kryteria pobierania
	 * @return tablica rekordów
	 * @throws UnsupportedEncodingException nieprawidłowe kodowanie
	 * @throws MalformedURLException nieprawidłowy url
	 * @throws IOException nie udało się odpytać tenanta
	 * @throws Exception baza nie istnieje
	 */
	public int getCount(HttpQueryParams params) throws UnsupportedEncodingException, MalformedURLException, IOException, Exception {
		String url = params.getTenantUrl() + "/api/z3950server/count?" + prepareGetParams(params, null, null);

		GetMethod method = new GetMethod(url);

//		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
//				new DefaultHttpMethodRetryHandler(3, false));

		try {
			if (client.executeMethod(method) == HttpStatus.SC_OK) {
				try {
					return Integer.parseInt(method.getResponseBodyAsString(10));
				} catch (NumberFormatException ex) {
					throw new Exception("Database not found");
				}
			} else {
				throw new Exception("Database not found");
			}
		} finally {
			method.releaseConnection();
		}
	}

	/**
	 * Pobiera listę rekordów z bazy tenenta
	 * 
	 * @param params kryteria pobierania
	 * @param start pierwszy rekord
	 * @param limit liczba rekordów
	 * @return tablica rekordów
	 * @throws UnsupportedEncodingException nieprawidłowe kodowanie
	 * @throws MalformedURLException nieprawidłowy url
	 * @throws IOException nie udało się odpytać tenanta
	 * @throws Exception baza nie istnieje
	 */
	public Record[] getList(HttpQueryParams params, Integer start, Integer limit) throws UnsupportedEncodingException, MalformedURLException, IOException, Exception {
		String url = params.getTenantUrl() + "/api/z3950server/?" + prepareGetParams(params, start, limit);

		GetMethod method = new GetMethod(url);

//		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
//				new DefaultHttpMethodRetryHandler(3, false));

		try {
			if (client.executeMethod(method) == HttpStatus.SC_OK) {
				List<Record> list = new ArrayList<>();

				MarcReader reader = new MarcStreamReader(method.getResponseBodyAsStream(), "UTF8");
				while (reader.hasNext()) {
					list.add(reader.next());
				}

				Record[] records = new Record[limit];
				return (Record[]) list.toArray(records);
			} else {
				throw new Exception("Database not found");
			}
		} finally {
			method.releaseConnection();
		}
	}

	/**
	 * Wydziela z nazwy kolekcji nazwę tenanta i id bazy
	 *
	 * @param molnetBaseProtocol protokół wykorzystywany we wdrożeniu
	 * @param molnetBaseDomain domena aplikacji
	 * @param collection nazwa bazy (nazwa tenenta)
	 * @param params parametry zapytania
	 * @throws java.lang.Exception nie udało się utworzyć urla do tenanta
	 */
	public void parseDbName(String molnetBaseProtocol, String molnetBaseDomain, String collection, HttpQueryParams params) throws Exception {
		if (molnetBaseDomain == null || collection == null) {
			throw new Exception("Cannot create tenant url");
		}

		if (molnetBaseProtocol == null) {
			molnetBaseProtocol = "http";
		}

		String tenantName = collection;
		int index = collection.indexOf("/");
		if (index != -1) {
			params.setBibliographicDbAlias(collection.substring(index + 1));
			tenantName = tenantName.substring(0, index);
		}

		//tworzenie urla tenenta
		params.setTenantUrl(String.format("%s://%s.%s", molnetBaseProtocol, tenantName, molnetBaseDomain));
	}

	/**
	 * Przetwarza zapytanie z bib-1 na queryParams
	 * 
	 * @param qn zapytanie bib-1
	 * @param params parametry queryParams
	 * @throws Exception nie udało się przetworzyć zapytania
	 */
	//TODO dodać sprawdzanie and or
	public void parseQuery(QueryNode qn, HttpQueryParams params) throws Exception {

		if (qn instanceof InternalModelRootNode) {
			InternalModelRootNode imrn = (InternalModelRootNode) qn;
			parseQuery(imrn.getChild(), params);
		} else if (qn instanceof InternalModelNamespaceNode) {
			InternalModelNamespaceNode imns = (InternalModelNamespaceNode) qn;
			attrset = imns.getAttrset();
			parseQuery(imns.getChild(), params);
		} else if (qn instanceof ComplexNode) {
			ComplexNode cn = (ComplexNode) qn;
			parseQuery(cn.getLHS(), params);
			parseQuery(cn.getRHS(), params);
		} else if (qn instanceof AttrPlusTermNode) {
			AttrPlusTermNode aptn = (AttrPlusTermNode) qn;
			Object a = aptn.getAttr("AccessPoint");

			if (a != null) {
				if (a instanceof AttrValue) {
					AttrValue av = (AttrValue) a;
					String val = av.getWithDefaultNamespace(attrset);

					if (patternTitle.matcher(val).matches()) {
						params.setTitle((String) aptn.getTerm());
					} else if (patternAuthor.matcher(val).matches()) {
						params.setAuthor((String) aptn.getTerm());
					} else if (patternIsbn.matcher(val).matches()) {
						params.setIsbn((String) aptn.getTerm());
					} else if (patternPublDate.matcher(val).matches()) {
						params.setPublDate((String) aptn.getTerm());
					}
				} else {
					throw new Exception("Cannot parse query");
				}
			} else {
				throw new Exception("Cannot parse query");
			}
		}
	}
}
