package pl.mol.molnet.z3950.server;

import org.jzkit.search.DeduplicationModel;
import org.jzkit.search.LandscapeSpecification;
import org.jzkit.search.SortModel;
import org.jzkit.search.impl.SearchSessionImpl;
import org.jzkit.search.provider.iface.SearchException;
import org.jzkit.search.util.Profile.ProfileService;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.RecordBuilder.RecordBuilderService;
import org.jzkit.search.util.RecordConversion.FragmentTransformerService;
import org.jzkit.search.util.RecordModel.RecordFormatSpecification;
import org.jzkit.search.util.ResultSet.IRResultSetInfo;
import org.jzkit.search.util.ResultSet.IRResultSetStatus;
import org.jzkit.search.util.ResultSet.TransformingIRResultSet;

/**
 * Nadpisuje funkcję wyszukującą, zbiera błędy i zamienia na diag.
 *
 * @author Paweł
 */
public class SearchSessionDynamicCollectionImpl extends SearchSessionImpl {

    protected SearchSessionDynamicCollectionImpl(ProfileService profile_service,
            FragmentTransformerService fts,
            RecordBuilderService rbs) throws SearchException {
        super(profile_service, fts, rbs);
    }

	/**
	 * Punkt pośredni w search, odczytuje błędy z resultset i rzuca odpowiednie wyjątki
	 * 
	 * @param landscape
	 * @param model
	 * @param deduplication_model
	 * @param sort_model
	 * @param rfs
	 * @return
	 * @throws SearchException 
	 */
    @Override
    public TransformingIRResultSet search(LandscapeSpecification landscape,
            QueryModel model,
            DeduplicationModel deduplication_model,
            SortModel sort_model,
            RecordFormatSpecification rfs) throws SearchException {

        TransformingIRResultSet result = super.search(landscape, model, deduplication_model, sort_model, rfs);

        if (result.getStatus() == IRResultSetStatus.FAILURE) {
            if ("".equals(((IRResultSetInfo) result.getResultSetInfo().record_sources.get(0)).getResultSetName())) {
                throw new SearchException("Unknown Collection " + landscape.toString(), SearchException.UNKOWN_COLLECTION);
            } else {
                throw new SearchException(((IRResultSetInfo) result.getResultSetInfo().record_sources.get(0)).getResultSetName(), SearchException.INTERNAL_ERROR);
            }
        }

        return result;
    }
}
