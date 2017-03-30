package com.six.compactdb.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.six.compactdb.dialect.Dialect;

public class ObjectWrapper {
	private Object object = null;
	private Dialect dialect = null;

	public ObjectWrapper(Object object, Dialect dialect) {
		this.object = object;
		this.dialect = dialect;
	}

	public boolean isArray() {
		return this.object.getClass().isArray();
	}

	public boolean isNotArray() {
		return !this.object.getClass().isArray();
	}

	public int length() {
		return this.stringValue().length();
	}

	public String toLowerCase() {
		return this.stringValue().toLowerCase();
	}

	public String toUpperCase() {
		return this.stringValue().toUpperCase();
	}

	public String trim() {
		return this.stringValue().trim();
	}

	public String substring(int beginIndex, int endIndex) {
		return this.stringValue().substring(beginIndex, endIndex);
	}

	public String beginDate() {
		return this.dialect.dateValue(this.toDate("yyyy-MM-dd HH:mm:ss",
				"1900-01-01 00:00:00"));
	}

	public String beginDate(String format) {
		return this.dialect.dateValue(this
				.toDate(format, "1900-01-01 00:00:00"));
	}

	public String endDate() {
		return this.dialect.dateValue(this.toDate("yyyy-MM-dd HH:mm:ss",
				"9999-12-31 23:59:59"));
	}

	public String endDate(String format) {
		return this.dialect.dateValue(this
				.toDate(format, "9999-12-31 23:59:59"));
	}

	public String date() {
		return this.dialect.dateValue(this.toDate("yyyy-MM-dd HH:mm:ss", ""));
	}

	public String date(String format) {
		return this.dialect.dateValue(this.toDate(format, ""));
	}

	public String date(String format, String def) {
		return this.dialect.dateValue(this.toDate(format, def));
	}

	private String toDate(String format, String def) {
		String value = null;
		if (this.object instanceof Date) {
			value = (new SimpleDateFormat(format)).format((Date) this.object);
		} else {
			value = this.stringValue();
			Date dataValue = EnergydbActionUtil.parseDate(value);
			if (dataValue != null) {
				value = (new SimpleDateFormat(format)).format(dataValue);
			}
		}

		if (value == null || value.equals("")) {
			value = def;
		}

		return value;
	}

	public boolean isNull() {
		return this.object == null;
	}

	public String isNull(String def) {
		return this.isNull() ? def : this.stringValue();
	}

	public boolean isNotNull() {
		return this.object != null;
	}

	public boolean isEmpty() {
		return this.stringValue().equals("");
	}

	public String isEmpty(String def) {
		return this.isEmpty() ? def : this.stringValue();
	}

	public boolean isNotEmpty() {
		return !this.stringValue().equals("");
	}

	public boolean equals(String str) {
		return this.stringValue().equals(str);
	}

	public boolean notEquals(String str) {
		return !this.stringValue().equals(str);
	}

	public boolean isIp() {
		String ip = this.stringValue();
		return Pattern.matches("\\d{1,3}(\\.\\d{1,3}){3}", ip);
	}

	public String ip() {
		return this.ip("");
	}

	public String ip(String def) {
		String ip = this.stringValue();
		if (ip != null && !ip.equals("")) {
			if (!this.isIp()) {
				return ip;
			} else {
				String newip = "";
				String[] value = ip.split("\\.");

				for (int i = 0; i < value.length; ++i) {
					newip = newip + this.fillStr(value[i], 3, "0") + ".";
				}

				return newip.substring(0, newip.length() - 1);
			}
		} else {
			return def;
		}
	}

	public String ipHex() {
		String ip = this.stringValue();
		if (this.isIp()) {
			String hex = "";
			String[] value = ip.split("\\.");

			for (int i = 0; i < value.length; ++i) {
				int d = Integer.parseInt(value[i]);
				String hexstr = Integer.toHexString(d);
				if (hexstr.length() == 1) {
					hexstr = "0" + hexstr;
				}

				hex = hex + hexstr;
			}

			return hex.toUpperCase();
		} else {
			return ip;
		}
	}

	public String ipLong() {
		if (this.isIp()) {
			String hex = this.ipHex();
			return String.valueOf(Long.parseLong(hex, 16));
		} else {
			return "";
		}
	}

	public String inValue() {
		StringBuffer data = new StringBuffer();
		int i;
		if (this.object instanceof int[]) {
			int[] v = (int[]) this.object;

			for (i = 0; v != null && i < v.length; ++i) {
				if (data.length() == 0) {
					data.append("(").append(v[i]);
				} else {
					data.append(",").append(v[i]);
				}
			}

			if (data.length() > 0) {
				data.append(")");
			}
		} else if (this.object instanceof String[]) {
			String[] var4 = (String[]) this.object;

			for (i = 0; var4 != null && i < var4.length; ++i) {
				if (data.length() == 0) {
					data.append("(\'").append(var4[i]).append("\'");
				} else {
					data.append(",\'").append(var4[i]).append("\'");
				}
			}

			if (data.length() > 0) {
				data.append(")");
			}
		} else {
			data.append("(\'").append(this.stringValue()).append("\')");
		}

		return data.length() > 0 ? data.toString() : "";
	}

	private String fillStr(String str, int length, String filestr) {
		String newstr = str;
		if (str.length() < length) {
			for (int i = 0; i < length - str.length(); ++i) {
				newstr = filestr + newstr;
			}
		}

		return newstr;
	}

	private String stringValue() {
		if (this.object == null) {
			return "";
		} else if (this.object instanceof Object[]) {
			Object obj = ((Object[]) this.object)[0];
			return obj != null ? obj.toString() : "";
		} else {
			return this.object.toString();
		}
	}

	public String toString() {
		String str = this.stringValue();
		return str == null ? "" : str.replaceAll("\'", "\'\'");
	}
}
