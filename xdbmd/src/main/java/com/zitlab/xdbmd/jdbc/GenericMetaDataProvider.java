package com.zitlab.xdbmd.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Predicate;

public abstract class GenericMetaDataProvider implements MetaDataProvider {

	protected DatabaseMetaData databaseMetaData;

	public GenericMetaDataProvider(DatabaseMetaData dbmd) {
		this.databaseMetaData = dbmd;
	}

	@Override
	public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
		return databaseMetaData.getPrimaryKeys(catalog, schema, table);
	}

	@Override
	public ResultSet getTables(String schemaPattern, String tableNamePattern)
			throws SQLException {
		String[] types = { "TABLE", "VIEW" };
		return databaseMetaData.getTables(null, schemaPattern, tableNamePattern, types);
	}

	@Override
	public ResultSet getSchemas() throws SQLException {
		return databaseMetaData.getSchemas();
	}

	@Override
	public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
		return databaseMetaData.getImportedKeys(catalog, schema, table);
	}

	@Override
	public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique) throws SQLException {
		return databaseMetaData.getIndexInfo(catalog, schema, table, unique, true);
	}

	@Override
	public ResultSet getUniqueIndexInfo(String catalog, String schema, String table) throws SQLException {
		return databaseMetaData.getIndexInfo(catalog, schema, table, true, true);
	}

	protected ResultSet query(String selectQuery, Object... params) throws SQLException {
		PreparedStatement ps = databaseMetaData.getConnection().prepareStatement(selectQuery);
		int size = params.length;
		for(int i =0; i < size; i++) {
			ps.setObject(i+1, params[i]);
		}
		return ps.executeQuery();
	}
	
	public boolean supports() throws SQLException{
		return getUrlPredicate().test(databaseMetaData.getURL());
	}

	protected abstract Predicate<String> getUrlPredicate();
}
