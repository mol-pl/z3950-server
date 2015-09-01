
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;
import org.marc4j.marc.Record;
import pl.mol.molnet.z3950.server.HttpDataProvider;
import pl.mol.molnet.z3950.server.HttpQueryParams;

/**
 *
 * @author Paweł
 */
@Ignore
public class TestITCase {

	private String url = "http://test.localhost:8080/api/z3950server/?author=a&start=0&limit=100";
	//private String url = "https://mol-prod--beta.molnet.mol.pl/api/z3950server/?author=arctowa&start=0&limit=1";
	//private String url = "http://dev.mol.com.pl/api/z3950server/?author=a&start=0&limit=10";

	@Test
	public void test() throws Exception {

		HttpDataProvider httpDataProvider = new HttpDataProvider();
		HttpQueryParams params = new HttpQueryParams();
		httpDataProvider.parseDbName("http", "localhost:8080", "test", params);
		params.setAuthor("a");

		int count = httpDataProvider.getCount(params);

		Record[] list = httpDataProvider.getList(params, 0, count);
		assertNotNull(list);
	}
}
