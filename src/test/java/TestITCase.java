
import java.io.IOException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author Paweł
 */
public class TestITCase {

	@Test
	public void test() throws IOException, SAXException {
		String[] isbns = new String[6];
		isbns[0] = "83-01-01373-1";
		isbns[1] = "978-83-01-01373-7";
		isbns[2] = "978-83-01-01373-X";
		isbns[3] = "83-01-01373-7 coś";
		isbns[4] = "coś 83-01-01373-7 coś";
		isbns[5] = "coś 83-01-01373-1 coś  83-01-01373-2";
	}
}
