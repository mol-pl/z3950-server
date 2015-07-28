
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

        String s = getNormalizedISBN(isbns);
    }

    private String getNormalizedISBN(String[] isbnFieldList) {
        Pattern pattern = Pattern.compile("[^0-9X]+");
        if (isbnFieldList != null) {
            for (String isbnField : isbnFieldList) {
                if (isbnField != null) {
                    String[] splitedIsbn = isbnField.split(" ");
                    //dla każdego wyłuskanego po spacji isbn
                    for (String s : splitedIsbn) {
                        //usuwanie z ciągu znków - zostają tylko cyfry i X
                        String normalizedIsbn = pattern.matcher(s).replaceAll("");

                        if (normalizedIsbn.startsWith("978")) {
                            //zwracaj 13 znaków
                            if (normalizedIsbn.length() >= 13) {
                                return normalizedIsbn.substring(0, 13);
                            }
                        } else if (normalizedIsbn.length() >= 10) {
                            //zwracja 10 znaków
                            return normalizedIsbn.substring(0, 10);
                        }
                    }
                } else {
                    return null;
                }
            }
            return null;
        } else {
            return null;
        }
    }
}
