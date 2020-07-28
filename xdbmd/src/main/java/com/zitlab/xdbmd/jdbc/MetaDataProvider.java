package com.zitlab.xdbmd.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface MetaDataProvider {
	ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException;

	ResultSet getTables(String schemaPattern, String tableNamePattern)
			throws SQLException;
	
	ResultSet getColumns(String schemaPattern, String tableNamePattern)
			throws SQLException;

	ResultSet getSchemas() throws SQLException;

	ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException;

	ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique)
			throws SQLException;
	
	public ResultSet getUniqueIndexInfo(String catalog, String schema, String table)
			throws SQLException;
}
