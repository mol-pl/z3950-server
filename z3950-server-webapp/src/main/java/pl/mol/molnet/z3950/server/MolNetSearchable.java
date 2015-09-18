package pl.mol.molnet.z3950.server;

import java.util.*;

import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.QueryModel.InvalidQueryException;
import org.springframework.context.*;

/**
 * Zarządza wyszukiwaniem danych.
 *
 * @author Paweł
 */
public class MolNetSearchable implements Searchable {

	private Map record_archetypes = new HashMap();
	private ApplicationContext ctx;
	private String baseDomain;
	private String baseProtocol;
	private HttpDataProvider httpDataProvider;

	public MolNetSearchable() {
		httpDataProvider = new HttpDataProvider();
	}

	public MolNetSearchable(String baseDomain, String baseProtocol) {
		this();
		this.baseDomain = baseDomain;
		this.baseProtocol = baseProtocol;
	}

	public void setBaseDomain(String baseDomain) {
		this.baseDomain = baseDomain;
	}

	public void setBaseProtocol(String baseProtocol) {
		this.baseProtocol = baseProtocol;
	}

	public String getBaseDomain() {
		return baseDomain;
	}

	public String getBaseProtocol() {
		return baseProtocol;
	}

	@Override
	public void close() {
	}

	@Override
	public IRResultSet evaluate(IRQuery q) {
		return evaluate(q, null, null);
	}

	@Override
	public IRResultSet evaluate(IRQuery q, Object user_info) {
		return evaluate(q, user_info, null);
	}

	/**
	 * PRzetwarza zapytanie i inicjuje wyszukiwanie danych.
	 * 
	 * @param q zapytanie
	 * @param user_info informacje o użytkowniku
	 * @param observers wątki kontrolujące wykoanie zapytania
	 * @return resultset
	 */
	@Override
	public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers) {
		String molnetBaseDomain = (String) ctx.getBean(this.baseDomain);
		String molnetBaseProtocol = (String) ctx.getBean(this.baseProtocol);

		//parsuj zapytanie
		HttpQueryParams params = new HttpQueryParams();
		try {
			httpDataProvider.parseQuery(q.getQueryModel().toInternalQueryModel(ctx), params);
		} catch (InvalidQueryException ex) {
			//nie udało się przetworzyć zapytania, zwracaj pusty resultset z błędem
			return notifyError("Syntax error while parsing CQL query");
		} catch (Exception ex) {
			return notifyError(ex.getMessage());
		}

		//pobierz nazwę tenanta i id bazy
		try {
			httpDataProvider.parseDbName(molnetBaseProtocol, molnetBaseDomain, (String) q.getCollections().get(0), params);
		} catch (Exception ex) {
			return notifyError(ex.getMessage());
		}

		//przygotowane parametry, można szukać
		try {
			MolNetResultSet result = new MolNetResultSet(params);
			result.setStatus(IRResultSetStatus.COMPLETE);
			return result;
		} catch (Exception ex) {
			return notifyError("");
		}
	}

	/**
	 * Konstruuje informację o błędzie
	 * Kod błędu jest wrzucany w nazwę zbioru danych, potem jest stamtąd wyciągany
	 * 
	 * @param message kod/wiadomość błędu
	 * @return resultset
	 */
	private IRResultSet notifyError(String message) {
		MolNetResultSet result = new MolNetResultSet();
		result.setStatus(IRResultSetStatus.FAILURE);
		result.setResultSetName(message);
		return result;
	}

	@Override
	public void setRecordArchetypes(Map record_syntax_archetypes) {
		this.record_archetypes = record_syntax_archetypes;
	}

	@Override
	public Map getRecordArchetypes() {
		return record_archetypes;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) {
		this.ctx = ctx;
	}
}
