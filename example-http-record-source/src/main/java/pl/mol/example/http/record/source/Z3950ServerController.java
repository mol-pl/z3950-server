package pl.mol.example.http.record.source;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Testowy kontroler źródło rekordów w formacie iso2709 dla serwera z39.50
 *
 * @author Paweł
 */
@Controller
@RequestMapping(value = "/api/z3950server")
public class Z3950ServerController {

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] list(
            @RequestParam(value = "bibdbalias", required = false) String bibDbAlias,
            @RequestParam(value = "start", defaultValue = "0") int index,
            @RequestParam(value = "limit", defaultValue = "10") int count,
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "date", required = false) String date) throws IOException {

        return IOUtils.toByteArray(Z3950ServerController.class.getResourceAsStream("/test.mrc"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public int count(@RequestParam(value = "bibdbalias", required = false) String bibDbAlias,
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "date", required = false) String date) {

        return 1;
    }
}
