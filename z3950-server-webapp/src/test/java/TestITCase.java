
import org.junit.Ignore;
import org.junit.Test;
import org.jzkit.z3950.server.Z3950Listener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Do testów ręcznych
 *
 * @author Paweł
 */
//@Ignore
public class TestITCase {

	@Test
	public void test() throws Exception {
		ApplicationContext app_context = new ClassPathXmlApplicationContext("applicationContext.xml");
		Z3950Listener listener = (Z3950Listener) app_context.getBean("Z3950Listener", Z3950Listener.class);
		listener.start();
		Thread.sleep(1000000);
	}
}
