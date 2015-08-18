package com.k_int.sql.data_dictionary;

/**
 * Title:       RelatedRecordCollection
 * @version:    $Id: RelatedRecordCollection.java,v 1.2 2004/10/27 15:07:32 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description: A set of entities that have a FK attribute matching some other
 *              entity PK attribute.
 *              For example, table O_ORDER has PK O_ID and ORDERITEM has FK OI_O_ID
 *              Then the ORDER attribute _ORDERITEM_Ref will be an instance of RelatedRecordCollection
 *              and will be a vector containing entities for all rows where OI_O_ID = O_ID
 */
public class RelatedRecordCollection extends QueryBasedRecordCollection
{
  private Entity parent = null;
  private CollectionAttribute parent_link_attr = null;

  public RelatedRecordCollection(Entity parent,
                                 String base_table_name,
                                 CollectionAttribute link_attr) throws UnknownAccessPointException,
                                                                       UnknownCollectionException
  {
    super(parent.getPersistenceContext(), base_table_name);
    this.parent=parent;
    this.parent_link_attr = link_attr;
  }

  public RelatedRecordCollection(Entity parent,
                                 String base_table_name,
                                 EntityKey criteria,
                                 CollectionAttribute link_attr) throws UnknownAccessPointException,
                                                                       UnknownCollectionException
  {
    super(parent.getPersistenceContext(), base_table_name, criteria);
    this.parent=parent;
    this.parent_link_attr = link_attr;
  }

  public Object getPseudoAttr(String attrname)
  {
    if ( attrname.equals("size") )
    {
      return new Integer(size());
    }
    
    return "";
  }

  public int createEntityForCollection()
  {
    // We are creating a new member for this collection, just
    // like creating a normal new entity *except* we try and
    // pull foreign key references from the PK of the parent table

    // First off, just create the related record (N.B. Constructor will
    // register entity with the default persistence context)
    Entity new_entity = Entity.create(base_et, ctx);

    // Now the difficult bit.. Try and figure out what attributes
    // in the parent relate to attributes in this new Entity.
    parent.pushPKToRelatedRecord(parent_link_attr, new_entity);

    // Finally, add the new member
    return add(new_entity);
  }
}
