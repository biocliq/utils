package com.zitlab.xdbmd.plugin.mariadb;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Predicate;

import com.zitlab.xdbmd.jdbc.GenericMetaDataProvider;

public class MariadbMetaDataProvider extends GenericMetaDataProvider {

	public MariadbMetaDataProvider(DatabaseMetaData dbmd) {
		super(dbmd);
	}
	
	@Override
	public ResultSet getTables(String schemaPattern, String tableNamePattern)
			throws SQLException {
		String _schemaPattern = (null == schemaPattern)  ? "%" : schemaPattern;
		String _tablePattern = (null == tableNamePattern) ? "%" : tableNamePattern;
		String[] types = { "TABLE", "VIEW" };
		return databaseMetaData.getTables( _schemaPattern,null, _tablePattern, types);
	}

	@Override
	protected Predicate<String> getUrlPredicate() {
		return connectionUrl -> (connectionUrl.startsWith("jdbc:mariadb:"));
	}
	
	@Override
	public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException{
		return databaseMetaData.getPrimaryKeys(schema, "%", table);
	}
	
	@Override
	public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique) throws SQLException {
		return databaseMetaData.getIndexInfo(schema, "%", table, unique, true);
	}
	
	@Override
	public ResultSet getColumns(String schemaPattern, String tableNamePattern)
			throws SQLException{
		return databaseMetaData.getColumns(schemaPattern, "%", tableNamePattern, "%");
	}
	
//	@Override
//	public ResultSet getTables(String schemaPattern, String tableNamePattern)
//			throws SQLException {
//		String _schemaPattern = (null == schemaPattern)  ? "%" : schemaPattern;
//		String _tablePattern = (null == tableNamePattern) ? "%" : tableNamePattern;		
//		return query(DBQuery.GET_TABLES, _schemaPattern, _tablePattern);
//	}
	
	public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
		String _schemaPattern = (null == schema)  ? "%" : schema;
		String _tablePattern = (null == table) ? "%" : table;
		return query(DBQuery.GET_IMPORTED_KEYS, _schemaPattern, _tablePattern);
	}
}
