import com.k_int.sql.qm_to_sql.Gazeteer;

public class TestGazeteer implements com.k_int.sql.qm_to_sql.Gazeteer {

  public TestGazeteer() { 
  }

  public String lookupWKTForPlaceName(String place_name) {
    System.err.println("Resolving place name "+place_name);
    return "Polygon((0 0,0 10000,10000 10000,10000 0,0 0))";
  }

  public String lookupWKTForNearPlaceName(String place_name, long near) {
    System.err.println("Resolving place name "+place_name);
    return "Polygon((0 0,0 10000,10000 10000,10000 0,0 0))";
  }
}
