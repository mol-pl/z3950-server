package pl.mol.molnet.z3950.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
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
 * @author Pawe�
 */
public class HttpDataProvider {

    private final Pattern patternTitle = Pattern.compile("bib-1\\.1\\.4");
    private final Pattern patternAuthor = Pattern.compile("bib-1\\.1\\.1003");
    private final Pattern patternIsbn = Pattern.compile("bib-1\\.1\\.7");
    private final Pattern patternPublDate = Pattern.compile("bib-1\\.1\\.31");

    private String attrset;

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

        if (params.getLibraryId() != null) {
            if (urlParameters != null) {
                urlParameters = urlParameters + "&library=" + URLEncoder.encode(params.getLibraryId().toString(), "UTF-8");
            } else {
                urlParameters = "library=" + URLEncoder.encode(params.getLibraryId().toString(), "UTF-8");
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

    public int getCount(HttpQueryParams params) throws UnsupportedEncodingException, MalformedURLException, IOException, Exception {
        String url = params.getTenantUrl() + "/api/z3950server/count?" + prepareGetParams(params, null, null);

        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("GET");

        if (con.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine = in.readLine();
            in.close();

            try {
                return Integer.parseInt(inputLine);
            } catch (NumberFormatException ex) {
                throw new Exception("Database not found");
            }
        } else {
            throw new Exception("Database not found");
        }
    }

    public Record[] getList(HttpQueryParams params, Integer start, Integer limit) throws UnsupportedEncodingException, MalformedURLException, IOException, Exception {
        String url = params.getTenantUrl() + "/api/z3950server/?" + prepareGetParams(params, start, limit);

        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("GET");

        if (con.getResponseCode() == 200) {
            List<Record> list = new ArrayList<>();
            MarcReader reader = new MarcStreamReader(con.getInputStream(), "UTF8");
            while (reader.hasNext()) {
                list.add(reader.next());
            }

            Record[] records = new Record[limit];
            return (Record[]) list.toArray(records);
        } else {
            throw new Exception("Database not found");
        }
    }

    /**
     * Wydziela z nazwy kolekcji nazw� tenanta i id bazy
     *
     * @param molnetUrlDomain
     * @param collection
     * @param params
     * @throws java.lang.Exception
     */
    public void parseDbName(String molnetUrlDomain, String collection, HttpQueryParams params) throws Exception {
        if (molnetUrlDomain == null || collection == null) {
            throw new Exception("Cannot create tenant url");
        }

        String tenantName = collection;
        int index = collection.indexOf("/");
        if (index != -1) {
            try {
                params.setLibraryId(Long.parseLong(collection.substring(index + 1)));
            } catch (NumberFormatException ex) {
                throw new Exception("Invalid library identifier");
            }

            tenantName = tenantName.substring(0, index);
        }

        //tworzenie urla tenenta
        params.setTenantUrl(String.format("http://%s.%s", tenantName, molnetUrlDomain));
    }

    //TODO doda� sprawdzanie and or
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
