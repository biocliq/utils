package com.zitlab.xdbmd.plugin.postgres;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Predicate;

import com.zitlab.xdbmd.jdbc.GenericMetaDataProvider;

public class PostgresMetaDataProvider extends GenericMetaDataProvider {

	
	public PostgresMetaDataProvider(DatabaseMetaData dbmd) {
		super(dbmd);
	}
	
	@Override
	public ResultSet getTables(String schemaPattern, String tableNamePattern)
			throws SQLException {
		String _schemaPattern = (null == schemaPattern)  ? "%" : schemaPattern;
		String _tablePattern = (null == tableNamePattern) ? "%" : tableNamePattern;		
		return query(DBQuery.GET_TABLES, _schemaPattern, _tablePattern);
	}

	@Override
	protected Predicate<String> getUrlPredicate() {
		return connectionUrl -> (connectionUrl.startsWith("jdbc:postgresql:"));
	}	
	
	public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
		String _schemaPattern = (null == schema)  ? "%" : schema;
		String _tablePattern = (null == table) ? "%" : table;
		return query(DBQuery.GET_IMPORTED_KEYS, _schemaPattern, _tablePattern);
	}
	
}
