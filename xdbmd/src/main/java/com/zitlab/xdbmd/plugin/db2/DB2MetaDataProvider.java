package com.zitlab.xdbmd.plugin.db2;

import java.sql.DatabaseMetaData;
import java.util.function.Predicate;

import com.zitlab.xdbmd.jdbc.GenericMetaDataProvider;

public class DB2MetaDataProvider extends GenericMetaDataProvider {

	public DB2MetaDataProvider(DatabaseMetaData dbmd) {
		super(dbmd);
	}
	
	@Override
	protected Predicate<String> getUrlPredicate() {
		return connectionUrl -> (connectionUrl.startsWith("jdbc:oracle:"));
	}

}
