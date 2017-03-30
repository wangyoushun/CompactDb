package com.six.compactdb.model;

import com.six.compactdb.util.EnergydbActionUtil;

public class Column {
	private String name = null;
	private String propertyName = null;
	private boolean nullAble = true;
	private int length = 0;
	private int scale = 0;
	private int dataType = 0;
	private boolean primaryKey = false;

	public Column() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
		this.propertyName = EnergydbActionUtil.convertToPropertyName(name);
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	public boolean isNullAble() {
		return this.nullAble;
	}

	public void setNullAble(boolean nullAble) {
		this.nullAble = nullAble;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getScale() {
		return this.scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getDataType() {
		return this.dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
}
