package com.six.compactdb.dialect;

public class MysqlDialect implements Dialect {
	public MysqlDialect() {
	}

	public String getPrefixEsc() {
		return "`";
	}

	public String getSuffixEsc() {
		return "`";
	}

	public String getLimitQueryScript(String sql, int begin, int end) {
		return "SELECT * FROM (" + sql + ") T LIMIT " + (begin - 1) + ","
				+ (end - begin + 1);
	}

	public String dateValue(String date) {
		return "\'" + date + "\'";
	}

	public String getName() {
		return "mysql";
	}
}
