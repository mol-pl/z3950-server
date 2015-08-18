package com.k_int.sql.data_dictionary;

import java.sql.*;

/**
 * Title:       Entity
 * @version:    $Id: DefaultEntity.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 */
public class DefaultEntity extends Entity
{
    public DefaultEntity(EntityTemplate entity_metadata, PersistenceContext ctx)
    {
      super(entity_metadata, ctx);
    }

    public DefaultEntity(EntityTemplate entity_metadata,
                     PersistenceContext ctx,
		     ResultSet rs,
		     OID oid)
    {
      super(entity_metadata, ctx, rs, oid);
    }


    public void onNewRecord(PersistenceContext ctx)
    {
    }
}
