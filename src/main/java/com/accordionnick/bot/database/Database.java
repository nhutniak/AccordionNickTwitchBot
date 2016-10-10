package com.accordionnick.bot.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public abstract class Database
{
	protected final String TABLE_NAME;
	protected final Connection m_dbConnection;
	
	public Database( Connection conn, String tableName )
	{
		this.m_dbConnection = conn;
		TABLE_NAME = tableName;
	}

	protected Set<String> getDBTables(Connection targetDBConn) throws SQLException
	{
		Set<String> set = new HashSet<String>();
		DatabaseMetaData dbmeta = targetDBConn.getMetaData();
		readDBTable(set, dbmeta, "TABLE", null);
		readDBTable(set, dbmeta, "VIEW", null);
		return set;
	}

	protected void readDBTable(Set<String> set, DatabaseMetaData dbmeta, String searchCriteria, String schema) throws SQLException
	{
		ResultSet rs = dbmeta.getTables(null, schema, null, new String[] { searchCriteria });
		while (rs.next())
		{
			set.add(rs.getString("TABLE_NAME").toLowerCase());
		}
	}

}
