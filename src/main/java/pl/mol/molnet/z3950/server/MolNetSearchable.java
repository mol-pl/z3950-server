package pl.mol.molnet.z3950.server;

import java.util.*;

import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.QueryModel.InvalidQueryException;
import org.springframework.context.*;

/**
 * Zarz�dza wyszukiwaniem danych.
 *
 * @author Pawe�
 */
public class MolNetSearchable implements Searchable {

    private Map record_archetypes = new HashMap();
    private ApplicationContext ctx;
    private String settingsBeanName;
    private HttpDataProvider httpDataProvider;

    public MolNetSearchable() {
        httpDataProvider = new HttpDataProvider();
    }

    public MolNetSearchable(String settingsBeanName) {
        this();
        this.settingsBeanName = settingsBeanName;
    }

    public void setSettingsBeanName(String settingsBeanName) {
        this.settingsBeanName = settingsBeanName;
    }

    public String getSettingsBeanName() {
        return settingsBeanName;
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

    @Override
    public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers) {
        String molnetUrlDomain = (String)ctx.getBean(this.settingsBeanName);

        //parsuj zapytanie
        HttpQueryParams params = new HttpQueryParams();
        try {
            httpDataProvider.parseQuery(q.getQueryModel().toInternalQueryModel(ctx), params);
        } catch (InvalidQueryException ex) {
            //nie uda�o si� przetworzy� zapytania, zwracaj pusty resultset z b��dem
            return notifyError("Syntax error while parsing CQL query");
        } catch (Exception ex) {
            return notifyError(ex.getMessage());
        }

        //pobierz nazw� tenanta i id bazy
        try {
            httpDataProvider.parseDbName(molnetUrlDomain, (String) q.getCollections().get(0), params);
        } catch (Exception ex) {
            return notifyError(ex.getMessage());
        }

        //przygotowane parametry, mo�na szuka�
        try {
            MolNetResultSet result = new MolNetResultSet(params);
            result.setStatus(IRResultSetStatus.COMPLETE);
            return result;
        } catch (Exception ex) {
            return notifyError("");
        }
    }

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
