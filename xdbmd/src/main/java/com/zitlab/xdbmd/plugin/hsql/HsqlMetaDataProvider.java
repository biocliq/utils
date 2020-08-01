package com.zitlab.xdbmd.plugin.hsql;

import java.sql.DatabaseMetaData;
import java.util.function.Predicate;

import com.zitlab.xdbmd.jdbc.GenericMetaDataProvider;

public class HsqlMetaDataProvider extends GenericMetaDataProvider {

	public HsqlMetaDataProvider(DatabaseMetaData dbmd) {
		super(dbmd);
	}
	
	@Override
	protected Predicate<String> getUrlPredicate() {
		return connectionUrl -> (connectionUrl.startsWith("jdbc:hsql:"));
	}

}
