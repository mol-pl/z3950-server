package org.jzkit.util;

import org.jzkit.search.util.RecordModel.ExplicitRecordFormatSpecification;
import org.jzkit.a2j.codec.util.*;


// Try and hide the strangeness of Z39.50 syntax/schema/esn
public class FormatSpecOIDHelper
{
  // Convert an OID into a FormatSpecification.
  public static int[] getOID(OIDRegister reg, ExplicitRecordFormatSpecification spec)
  {
    // Step 1 : Look at the encoding: Some encodings have their own OID
    String encoding = spec.getEncoding().toString();
    if ( encoding != null )
    {
      // Because marc records combine "Syntax and Schema"
      if ( encoding.equals("iso2709") )
      {
        // We want to use the "schema" as an OID (USMARC, UKMARC, etc);
        OIDRegisterEntry entry = reg.lookupByName(spec.getSchema().toString());
        return entry.getValue();
      }
      else // Just lookup the type
      {
        OIDRegisterEntry entry = reg.lookupByName(encoding);
        return entry.getValue();
      }
    }

    return null;
  }

  public static ExplicitRecordFormatSpecification getSpec(OIDRegisterEntry entry, String esn, String profile)
  {
    if ( entry.getName().equals("usmarc") )
      return new ExplicitRecordFormatSpecification("iso2709:usmarc:"+esn);
    else if ( entry.getName().equals("ukmarc") )
      return new ExplicitRecordFormatSpecification("iso2709:ukmarc:"+esn);
    else if ( entry.getName().equals("grs-1") )
      return new ExplicitRecordFormatSpecification("grs-1:"+profile+":"+esn);
    else if ( entry.getName().equals("xml") )
      return new ExplicitRecordFormatSpecification("xml:null:"+esn);

    return null;
  }

  public static ExplicitRecordFormatSpecification getSpec(OIDRegister reg, int[] entry, String esn, String profile)
  {
    return getSpec(reg.lookupByOID(entry),esn,profile);
  }
}
