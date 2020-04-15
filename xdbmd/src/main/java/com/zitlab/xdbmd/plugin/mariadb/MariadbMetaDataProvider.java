package com.zitlab.xdbmd.plugin.mariadb;

import java.sql.DatabaseMetaData;
import java.util.function.Predicate;

import com.zitlab.xdbmd.jdbc.GenericMetaDataProvider;

public class MariadbMetaDataProvider extends GenericMetaDataProvider {

	public MariadbMetaDataProvider(DatabaseMetaData dbmd) {
		super(dbmd);
	}

	@Override
	protected Predicate<String> getUrlPredicate() {
		return connectionUrl -> (connectionUrl.startsWith("jdbc:postgresql:"));
	}
}
