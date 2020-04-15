package com.zitlab.xdbmd.plugin.postgres;

interface DBQuery {
	public static final String GET_TABLES = " SELECT (current_database()) AS table_cat," + 
			"    (nc.nspname) AS table_schem," + 
			"    (c.relname) AS table_name," + 
			"    (CASE" + 
			"            WHEN (nc.oid = pg_my_temp_schema()) THEN 'LOCAL TEMPORARY'" + 
			"            WHEN (c.relkind = ANY (ARRAY['r', 'p'])) THEN 'TABLE'" + 
			"            WHEN (c.relkind = 'v') THEN 'VIEW'" + 
			"            WHEN (c.relkind = 'f') THEN 'FOREIGN'" + 
			"            ELSE NULL" + 
			"        END) AS table_type," + 
			"       null remarks, " + 
			"    (CASE" + 
			"            WHEN (t.typname IS NOT NULL) THEN current_database()" + 
			"            ELSE NULL" + 
			"        END) AS type_cat," + 
			"    (nt.nspname) AS type_schem," + 
			"    (t.typname) AS type_name,    " + 
			"    (NULL) AS self_referencing_column_name," + 
			"    (NULL::character varying) AS reference_generation" + 
			"   FROM ((pg_namespace nc" + 
			"     JOIN pg_class c ON ((nc.oid = c.relnamespace)))" + 
			"     LEFT JOIN (pg_type t" + 
			"     JOIN pg_namespace nt ON ((t.typnamespace = nt.oid))) ON ((c.reloftype = t.oid)))" + 
			"  WHERE ((c.relkind = ANY (ARRAY['r', 'v', 'f', 'p'])) AND (NOT pg_is_other_temp_schema(nc.oid)) AND (pg_has_role(c.relowner, 'USAGE') " + 
			"  OR has_table_privilege(c.oid, 'SELECT, INSERT, UPDATE, DELETE, TRUNCATE, REFERENCES, TRIGGER') " + 
			"  OR has_any_column_privilege(c.oid, 'SELECT, INSERT, UPDATE, REFERENCES')))" + 
			"	AND   (c.relispartition is FALSE)" + 
			"  AND nc.nspname like ? AND c.relname  LIKE ? " + 
			"  AND c.relkind IN ('r', 'p', 'v') ";


	public static final String GET_IMPORTED_KEYS = "SELECT " + 
			"	current_database() AS PKTABLE_CAT," + 
			"   f_sch.nspname                                 AS PKTABLE_SCHEM," + 
			"   f_tbl.relname                                 AS PKTABLE_NAME," + 
			"   f_col.attname	PKCOLUMN_NAME, " +
			"   null as fktable_cat," + 
			"   sch.nspname                                   AS FKTABLE_SCHEM," + 
			"   tbl.relname                                   AS FKTABLE_NAME ," + 
			"   col.attname 									as FKCOLUMN_NAME," +  
			"   u.attposition KEY_SEQ, null update_rule, null delete_rule," + 
			"   c.conname                                 AS fk_name, null pk_name, 0  DEFERRABILITY" + 
			" FROM pg_constraint c" + 
			"       LEFT JOIN LATERAL UNNEST(c.conkey) WITH ORDINALITY AS u(attnum, attposition) ON TRUE" + 
			"       LEFT JOIN LATERAL UNNEST(c.confkey) WITH ORDINALITY AS f_u(attnum, attposition) ON f_u.attposition = u.attposition" + 
			"       JOIN pg_class tbl ON tbl.oid = c.conrelid" + 
			"       JOIN pg_namespace sch ON sch.oid = tbl.relnamespace" + 
			"       LEFT JOIN pg_attribute col ON (col.attrelid = tbl.oid AND col.attnum = u.attnum)" + 
			"       LEFT JOIN pg_class f_tbl ON f_tbl.oid = c.confrelid" + 
			"       LEFT JOIN pg_namespace f_sch ON f_sch.oid = f_tbl.relnamespace" + 
			"       LEFT JOIN pg_attribute f_col ON (f_col.attrelid = f_tbl.oid AND f_col.attnum = f_u.attnum)" + 
			"   where f_tbl.relkind  in ('r', 'p')" + 
			"   and f_tbl.relispartition is false" + 
			"   and tbl.relispartition  is false" + 
			"   and sch.nspname  like ? " + 
			"   and tbl.relname like ? " + 
			" GROUP BY fk_name, c.contype,PKCOLUMN_NAME , FKCOLUMN_NAME, PKTABLE_SCHEM, PKTABLE_NAME , FKTABLE_SCHEM, FKTABLE_NAME, u.attposition" + 
			" ORDER BY PKTABLE_SCHEM, PKTABLE_NAME ,fk_name, u.attposition";
}
