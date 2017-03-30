package com.six.compactdb.dialect;

public class OracleDialect implements Dialect {
	public OracleDialect() {
	}

	public String getPrefixEsc() {
		return "\"";
	}

	public String getSuffixEsc() {
		return "\"";
	}

	public String getLimitQueryScript(String sql, int begin, int end) {
		return "SELECT * FROM (SELECT ROWNUM ROW_ID,T.* FROM (" + sql
				+ ") T WHERE ROWNUM<=" + end + ") WHERE ROW_ID>=" + begin;
	}

	public String dateValue(String date) {
		return "to_date(\'" + date + "\',\'yyyy-mm-dd hh24:mi:ss\')";
	}

	public String getName() {
		return "oracle";
	}
}
