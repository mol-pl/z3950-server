
import java.util.regex.Pattern;
import org.junit.Test;

/**
 *
 * @author Paweł
 */
public class TestITCase {
    /*
     Algorytm dodawania znaków umownych:
     dla każdego pola
     dla każdego podpola, sprawdzaj czy występuje za nim podpole, gdy tak to wstaw na końcu pola to co jest specyfikowane w podpolu
     */
    
    

    @Test
    public void test() {
        String[] isbns = new String[6];
        isbns[0] = "83-01-01373-1";
        isbns[1] = "978-83-01-01373-7";
        isbns[2] = "978-83-01-01373-X";
        isbns[3] = "83-01-01373-7 coś";
        isbns[4] = "coś 83-01-01373-7 coś";
        isbns[5] = "coś 83-01-01373-1 coś  83-01-01373-2";
    }
}
