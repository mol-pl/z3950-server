package pl.mol.molnet.z3950.server;

import org.jzkit.ServiceDirectory.*;
import org.jzkit.configuration.provider.xml.XMLImpl;

/**
 * Nadpisuje pobieranie informacji o dostępnej bazie danych.
 * Baza nie jest pobierana z pliku konfiguracyjnego, jest uzupełniana
 * domyślną konfiguracją i przekazywana do searchable.
 * Searchable próbuje szukać w podanej bazie, gdy okaże się że bazy nie ma
 * zgłaszany jest diag unknown db.
 *
 * @author Paweł
 */
public class XMLDynamicCollectionImpl extends XMLImpl {

	public XMLDynamicCollectionImpl(String config_file) {
		super(config_file);
	}

	@Override
	public CollectionDescriptionDBO lookupCollectionDescription(String collection_code) {
		CollectionDescriptionDBO coll = (CollectionDescriptionDBO) super.lookupCollectionDescription("default/library");
		//modyfikuj domyślne źródło na aktualnie wyszukiwane
		coll.setCode(collection_code);
		coll.setLocalId(collection_code);
		coll.getSearchServiceDescription().setCode(collection_code);
		return coll;
	}
}
