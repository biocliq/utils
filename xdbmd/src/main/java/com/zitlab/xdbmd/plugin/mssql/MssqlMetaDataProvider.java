package com.zitlab.xdbmd.plugin.mssql;

import java.sql.DatabaseMetaData;
import java.util.function.Predicate;

import com.zitlab.xdbmd.jdbc.GenericMetaDataProvider;

public class MssqlMetaDataProvider extends GenericMetaDataProvider {

	public MssqlMetaDataProvider(DatabaseMetaData dbmd) {
		super(dbmd);
	}
	
	@Override
	protected Predicate<String> getUrlPredicate() {
		return connectionUrl -> (connectionUrl.startsWith("jdbc:mssql:"));
	}

}
