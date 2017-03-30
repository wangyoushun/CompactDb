package com.six.compactdb;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.six.compactdb.util.EnergydbActionUtil;

public class DataValue extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public DataValue() {
    }

    public DataValue(Map<String, Object> values) {
        this.putAll(values);
    }

    public DataValue(String id, String value) {
        this.put((String)id, value);
    }

    public String getString(String id) {
        Object objValue = this.getObject(id);
        if(objValue != null) {
            if(objValue instanceof String) {
                return (String)objValue;
            } else if(objValue instanceof Object[]) {
                Object obj = ((Object[])objValue)[0];
                return obj != null?obj.toString():"";
            } else {
                return objValue.toString();
            }
        } else {
            return null;
        }
    }

    public Date getDate(String id) {
        Object objValue = this.getObject(id);
        if(objValue != null) {
            if(objValue instanceof Date) {
                return (Date)objValue;
            } else if(objValue instanceof Date[]) {
                return ((Date[])objValue)[0];
            } else {
                String strValue = this.getString(id);
                return strValue != null && !strValue.trim().equals("")?EnergydbActionUtil.parseDate(strValue):null;
            }
        } else {
            return null;
        }
    }

    public Timestamp getTimestamp(String id) {
        Date date = this.getDate(id);
        return date != null?new Timestamp(date.getTime()):null;
    }

    public String getDateString(String id, String format) {
        Date date = this.getDate(id);
        return date != null?(new SimpleDateFormat(format)).format(date):null;
    }

    public int getInt(String id) {
        Object objValue = this.getObject(id);
        if(objValue != null) {
            Number strValue1;
            if(objValue instanceof Number) {
                strValue1 = (Number)objValue;
                return strValue1.intValue();
            } else if(objValue instanceof Number[]) {
                strValue1 = ((Number[])objValue)[0];
                return strValue1.intValue();
            } else {
                String strValue = this.getString(id);
                return strValue != null && !strValue.trim().equals("")?Integer.valueOf(strValue).intValue():0;
            }
        } else {
            return 0;
        }
    }

    public Integer getInteger(String id) {
        Object objValue = this.getObject(id);
        if(objValue != null) {
            if(objValue instanceof Integer) {
                return (Integer)objValue;
            } else if(objValue instanceof Integer[]) {
                return ((Integer[])objValue)[0];
            } else {
                Number strValue1;
                if(objValue instanceof Number) {
                    strValue1 = (Number)objValue;
                    return new Integer(strValue1.intValue());
                } else if(objValue instanceof Number[]) {
                    strValue1 = ((Number[])objValue)[0];
                    return new Integer(strValue1.intValue());
                } else {
                    String strValue = this.getString(id);
                    return strValue != null && !strValue.trim().equals("")?Integer.valueOf(strValue):null;
                }
            }
        } else {
            return null;
        }
    }

    public Long getLong(String id) {
        Object objValue = this.getObject(id);
        if(objValue != null) {
            if(objValue instanceof Long) {
                return (Long)objValue;
            } else if(objValue instanceof Long[]) {
                return ((Long[])objValue)[0];
            } else {
                Number strValue1;
                if(objValue instanceof Number) {
                    strValue1 = (Number)objValue;
                    return new Long(strValue1.longValue());
                } else if(objValue instanceof Number[]) {
                    strValue1 = ((Number[])objValue)[0];
                    return new Long(strValue1.longValue());
                } else {
                    String strValue = this.getString(id);
                    return strValue != null && !strValue.trim().equals("")?Long.valueOf(strValue):null;
                }
            }
        } else {
            return null;
        }
    }

    public Float getFloat(String id) {
        Object objValue = this.getObject(id);
        if(objValue != null) {
            if(objValue instanceof Float) {
                return (Float)objValue;
            } else if(objValue instanceof Float[]) {
                return ((Float[])objValue)[0];
            } else {
                Number strValue1;
                if(objValue instanceof Number) {
                    strValue1 = (Number)objValue;
                    return new Float(strValue1.floatValue());
                } else if(objValue instanceof Number[]) {
                    strValue1 = ((Number[])objValue)[0];
                    return new Float(strValue1.floatValue());
                } else {
                    String strValue = this.getString(id);
                    return strValue != null && !strValue.trim().equals("")?Float.valueOf(strValue):null;
                }
            }
        } else {
            return null;
        }
    }

    public Double getDouble(String id) {
        Object objValue = this.getObject(id);
        if(objValue != null) {
            if(objValue instanceof Double) {
                return (Double)objValue;
            } else if(objValue instanceof Double[]) {
                return ((Double[])objValue)[0];
            } else {
                Number strValue1;
                if(objValue instanceof Number) {
                    strValue1 = (Number)objValue;
                    return new Double(strValue1.doubleValue());
                } else if(objValue instanceof Number[]) {
                    strValue1 = ((Number[])objValue)[0];
                    return new Double(strValue1.doubleValue());
                } else {
                    String strValue = this.getString(id);
                    return strValue != null && !strValue.trim().equals("")?Double.valueOf(strValue):null;
                }
            }
        } else {
            return null;
        }
    }

    public BigDecimal getBigDecimal(String id) {
        Object objValue = this.getObject(id);
        if(objValue != null) {
            if(objValue instanceof BigDecimal) {
                return (BigDecimal)objValue;
            } else if(objValue instanceof BigDecimal[]) {
                return ((BigDecimal[])objValue)[0];
            } else {
                Number strValue1;
                if(objValue instanceof Number) {
                    strValue1 = (Number)objValue;
                    return new BigDecimal(strValue1.doubleValue());
                } else if(objValue instanceof Number[]) {
                    strValue1 = ((Number[])objValue)[0];
                    return new BigDecimal(strValue1.doubleValue());
                } else {
                    String strValue = this.getString(id);
                    return strValue != null && !strValue.trim().equals("")?new BigDecimal(strValue):null;
                }
            }
        } else {
            return null;
        }
    }

    public String getNumberString(String id, String format) {
        Object objValue = this.getObject(id);
        if(objValue != null) {
            Number bigDecimal1;
            if(objValue instanceof Number) {
                bigDecimal1 = (Number)objValue;
                return (new DecimalFormat(format)).format(bigDecimal1);
            } else if(objValue instanceof Number[]) {
                bigDecimal1 = ((Number[])objValue)[0];
                return (new DecimalFormat(format)).format(bigDecimal1);
            } else {
                BigDecimal bigDecimal = this.getBigDecimal(id);
                return bigDecimal == null?null:(new DecimalFormat(format)).format(bigDecimal);
            }
        } else {
            return null;
        }
    }

    public byte[] getBytes(String id) {
        Object objValue = this.getObject(id);
        return objValue != null && objValue instanceof byte[]?(byte[])objValue:null;
    }

    public QueryResult getQueryResult(String id) {
        Object objValue = this.getObject(id);
        return objValue != null && objValue instanceof ResultSet?new QueryResult((ResultSet)objValue):null;
    }

    public Object getObject(String id) {
        return this.get(id) == null?"":this.get(id);
    }

    public DataValue setValue(String id, Object value) {
        this.put(id, value);
        return this;
    }

    public Object put(String key, Object value) {
        if(value == null) {
            value = "";
        }

        return super.put(key, value);
    }
}
