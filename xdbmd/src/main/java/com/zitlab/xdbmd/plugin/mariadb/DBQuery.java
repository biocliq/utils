package com.zitlab.xdbmd.plugin.mariadb;

interface DBQuery {

	public static final String GET_IMPORTED_KEYS = "SELECT" + 
			"     KCU.CONSTRAINT_CATALOG AS 'FKTABLE_CATALOG'" + 
			"   , KCU.CONSTRAINT_SCHEMA AS 'FKTABLE_SCHEM'" + 
			"   , KCU.CONSTRAINT_NAME AS 'FK_NAME'" + 
			"   , KCU.TABLE_NAME AS 'FKTABLE_NAME'" + 
			"   , KCU.COLUMN_NAME AS 'FKCOLUMN_NAME'" + 
			"   , KCU.ORDINAL_POSITION AS 'FK_ORDINAL_POSITION'" + 
			"   , RC.UNIQUE_CONSTRAINT_CATALOG AS 'PKTABLE_CATALOG'" + 
			"   , RC.UNIQUE_CONSTRAINT_SCHEMA AS 'PKTABLE_SCHEM'" + 
			"   , RC.UNIQUE_CONSTRAINT_NAME AS 'PK_NAME'" + 
			"   , KCU.REFERENCED_TABLE_NAME AS 'PKTABLE_NAME'" + 
			"   , KCU.REFERENCED_COLUMN_NAME AS 'PKCOLUMN_NAME'" + 
			"   , RC.DELETE_RULE AS 'DELETE_RULE'" + 
			"   , RC.UPDATE_RULE AS 'UPDATE_RULE'" + 
			"  FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS RC" + 
			"  INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE KCU" + 
			"  ON KCU.CONSTRAINT_CATALOG = RC.CONSTRAINT_CATALOG" + 
			"   AND KCU.CONSTRAINT_SCHEMA = RC.CONSTRAINT_SCHEMA" + 
			"   AND KCU.CONSTRAINT_NAME = RC.CONSTRAINT_NAME" + 
			"    WHERE kcu.constraint_schema LIKE ? " + 
			"  AND kcu.TABLE_NAME LIKE  ?";
}
