package com.biocliq.fluwiz.dataloader.xls;

public class Mapping {
	public static final int STRING = 0;
	public static final int INTEGER = 1;
	public static final int DOUBLE = 2;
	public static final int DATE = 3;
	public static final int DATETIME = 4;
	public static final int BIT = 5;
	
	private int column;
	private int dataType;
	private String dataTypeString;
	private String property;
	
	public Mapping(int col, String type, String property) {
		this.column = col;
		this.property = property;
		setDataType(type);
	}
	
	
	public void setDataType(String type){
		this.dataTypeString = type;
		if(null ==  type) {
			dataType = STRING;
			return;
		}
			
		switch (type.toLowerCase()) {
		case "integer":
		case "int":
			dataType = INTEGER;
			break;
		case "bit":
			dataType = BIT;
			break;
		case "number":
		case "double":
		case "float":
			dataType = DOUBLE;
			break;
		case "date":
			dataType = DATE;
			break;
		case "datetime":
			dataType = DATETIME;
			break;
		default:
			dataType = STRING;
			break;
		}
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public int getDataType() {
		return dataType;
	}	
}
