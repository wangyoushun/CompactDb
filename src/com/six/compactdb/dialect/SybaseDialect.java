package com.six.compactdb.dialect;

public class SybaseDialect implements Dialect {
	public SybaseDialect() {
	}

	public String getName() {
		return "sybase";
	}

	public String getPrefixEsc() {
		return "[";
	}

	public String getSuffixEsc() {
		return "]";
	}

	public String getLimitQueryScript(String sql, int begin, int end) {
		return "EXEC P_DIV_QUERY \'" + sql + "\'," + begin + "," + end;
	}

	public String dateValue(String date) {
		return "\'" + date + "\'";
	}
}
