package com.zitlab.xdbmd.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.function.Predicate;

import com.zitlab.xdbmd.plugin.mariadb.MariadbMetaDataProvider;
import com.zitlab.xdbmd.plugin.postgres.PostgresMetaDataProvider;

public class MetaDataProviderFactory {
	public static MetaDataProvider getMetaDataProvider(Connection con) throws SQLException{
		DatabaseMetaData dbmd = con.getMetaData();
		String db = dbmd.getDatabaseProductName().toLowerCase().trim();
		switch (db) {
		case "postgresql":
			return new PostgresMetaDataProvider(dbmd);
		case "mariadb":
		case "mysql":
			return new MariadbMetaDataProvider(dbmd);
		default:
			return new GenericMetaDataProvider(dbmd) {
				protected Predicate<String> getUrlPredicate() {
					return null;
				}
			};
		}
	}
}
