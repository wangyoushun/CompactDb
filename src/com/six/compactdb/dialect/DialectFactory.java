package com.six.compactdb.dialect;

import com.six.compactdb.exception.CompactdbException;

public class DialectFactory {
	public DialectFactory() {
	}

	public static Dialect getDialect(String type) {
		if (type.equals("oracle")) {
			return new OracleDialect();
		} else if (type.equals("sybase")) {
			return new SybaseDialect();
		} else if (type.equals("mysql")) {
			return new MysqlDialect();
		} else {
			throw new CompactdbException("不支持的数据库类型\"" + type + "\"");
		}
	}
}
