package com.zitlab.xdbmd.plugin.oracle;

import java.sql.DatabaseMetaData;
import java.util.function.Predicate;

import com.zitlab.xdbmd.jdbc.GenericMetaDataProvider;

public class OracleMetaDataProvider extends GenericMetaDataProvider {

	public OracleMetaDataProvider(DatabaseMetaData dbmd) {
		super(dbmd);
	}
	
	@Override
	protected Predicate<String> getUrlPredicate() {
		return connectionUrl -> (connectionUrl.startsWith("jdbc:oracle:"));
	}

}
