package org.jzkit.search.util.QueryModel.Internal;

import org.jzkit.search.util.QueryModel.*;

public class AttrType
{
    String attrset = null;
    Integer attrtype = null;

    public AttrType(String attrset, Integer attrtype)
    {
        this.attrset = attrset;
        this.attrtype = attrtype;
    }

    public AttrType(String attrset, int attrtype)
    {
        this(attrset, new Integer(attrtype));
    }

    public boolean equals(AttrType comp)  
    {
        if ( ( null != comp ) &&
             ( comp instanceof AttrType ) &&
             ( attrset.equals ( comp.getAttrSet() ) ) &&
             ( attrtype.equals ( comp.getAttrType() ) ) )
            return true;

        return false;
    }

    public String getAttrSet()
    {
        return attrset;
    }

    public Integer getAttrType()
    {
        return attrtype;
    }

    public String toString()
    {
        return attrset+"."+attrtype;
    }

    public int hashCode()
    {
        return toString().hashCode();
    }

}
