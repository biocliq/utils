package com.zitlab.xdbmd.plugin.h2;

import java.sql.DatabaseMetaData;
import java.util.function.Predicate;

import com.zitlab.xdbmd.jdbc.GenericMetaDataProvider;

public class H2MetaDataProvider extends GenericMetaDataProvider {

	public H2MetaDataProvider(DatabaseMetaData dbmd) {
		super(dbmd);
	}
	
	@Override
	protected Predicate<String> getUrlPredicate() {
		return connectionUrl -> (connectionUrl.startsWith("jdbc:h2:"));
	}

}
