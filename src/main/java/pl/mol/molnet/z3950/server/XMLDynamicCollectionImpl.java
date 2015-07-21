package pl.mol.molnet.z3950.server;

import org.jzkit.ServiceDirectory.*;
import org.jzkit.configuration.provider.xml.XMLImpl;

/**
 * Nadpisuje pobieranie informacji o dost�pnej bazie danych.
 * Baza nie jest pobierana z pliku konfiguracyjnego, jest uzupe�niana
 * domy�ln� konfiguracj� i przekazywana do searchable.
 * Searchable pr�buje szuka� w podanej bazie, gdy oka�e si� �e bazy nie ma
 * zg�aszany jest diag unknown db.
 * 
 * @author Pawe�
 */
public class XMLDynamicCollectionImpl extends XMLImpl {

    public XMLDynamicCollectionImpl(String config_file) {
        super(config_file);
    }

    @Override
    public CollectionDescriptionDBO lookupCollectionDescription(String collection_code) {
        CollectionDescriptionDBO coll = (CollectionDescriptionDBO) super.lookupCollectionDescription("default/library");
        //modyfikuj domy�lne �r�d�o na aktualnie wyszukiwane
        coll.setCode(collection_code);
        coll.setLocalId(collection_code);
        return coll;
    }
}
