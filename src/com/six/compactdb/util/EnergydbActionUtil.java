
package com.six.compactdb.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.six.compactdb.DataValue;
import com.six.compactdb.dialect.Dialect;
import com.six.compactdb.exception.CompactdbException;
import com.six.compactdb.model.Column;
import com.six.compactdb.util.ObjectWrapper;
import com.six.compactdb.util.StringTemplateLoader;

/**
 * 
 * @author iwantfly
 *
 */
public class EnergydbActionUtil {
    public EnergydbActionUtil() {
    }

    /**
     * ResultSet 转为 DataValue
     * @param rs
     * @return
     */
    public static List<DataValue> parseResultSet(ResultSet rs) {
        try {
        	List<DataValue> e = new ArrayList<DataValue>();
            List<Column> columns = getResultColumns(rs);

            while(rs.next()) {
                e.add(parseResult(rs, columns));
            }

            return e.size() > 0?e:null;
        } catch (Exception e) {
            throw new CompactdbException(e.getMessage());
        }
    }

    /**
     * ResultSet 转为 DataValue
     * @param rs
     * @param columns
     * @return
     */
    public static DataValue parseResult(ResultSet rs, List<Column> columns) {
        try {
            Map<String,Object> map = new HashMap<String,Object>();

            for(int i = 1; i <= columns.size(); ++i) {
                int type = ((Column)columns.get(i - 1)).getDataType();
                String name = ((Column)columns.get(i - 1)).getPropertyName();
                if(type != 93 && type != 91 && type != 92) {
                    Object value;
                    int index;
                    if(type != 2004 && type != -4 && type != -2) {
                        if(type != 2005) {
                            if(type != 4 && type != -6) {
                                if(type != 2 && type != 3) {
                                    if(type == 6) {
                                        map.put(name, Float.valueOf(rs.getFloat(i)));
                                    } else if(type == 8) {
                                    	map.put(name, Double.valueOf(rs.getDouble(i)));
                                    } else if(type == -5) {
                                    	map.put(name, Long.valueOf(rs.getLong(i)));
                                    } else {
                                        String var36 = rs.getString(i);
                                        if(var36 == null) {
                                        	map.put(name, var36);
                                        } else {
                                        	map.put(name, var36.trim());
                                        }
                                    }
                                } else {
                                	map.put(name, rs.getBigDecimal(i));
                                }
                            } else {
                            	map.put(name, new Integer(rs.getInt(i)));
                            }
                        } else {
                            value = rs.getObject(i);
                            if(value instanceof Clob) {
                                Clob var37 = (Clob)value;
                                Reader var38 = null;
                                if(var37 != null) {
                                    try {
                                        String var39 = "";
                                        char[] var40 = new char[1024];

                                        for(var38 = var37.getCharacterStream(); (index = var38.read(var40)) != -1; var39 = var39 + new String(var40, 0, index)) {
                                            ;
                                        }

                                        map.put(name, var39);
                                    } finally {
                                        if(var38 != null) {
                                            try {
                                                var38.close();
                                            } catch (IOException var30) {
                                                ;
                                            }
                                        }

                                    }
                                }
                            } else if(value != null) {
                            	map.put(name, value.toString());
                            }
                        }
                    } else {
                        value = rs.getObject(i);
                        if(value instanceof Blob) {
                            Blob data = (Blob)value;
                            InputStream reader = null;
                            ByteArrayOutputStream str = null;

                            try {
                                str = new ByteArrayOutputStream();
                                reader = data.getBinaryStream();
                                byte[] ac = new byte[1024];

                                while((index = reader.read(ac)) != -1) {
                                    str.write(ac, 0, index);
                                }

                                map.put(name, str.toByteArray());
                            } finally {
                                if(reader != null) {
                                    try {
                                        reader.close();
                                    } catch (IOException var32) {
                                        ;
                                    }
                                }

                                if(str != null) {
                                    try {
                                        str.close();
                                    } catch (IOException var31) {
                                        ;
                                    }
                                }

                            }
                        } else if(value instanceof byte[]) {
                        	map.put(name, value);
                        } else {
                        	map.put(name, value);
                        }
                    }
                } else {
                	map.put(name, rs.getTimestamp(i));
                }
            }

            return new DataValue(map);
        } catch (Exception e) {
            throw new CompactdbException(e.getMessage());
        }
    }

    /**
     * 首字符大写
     * @param str
     * @return
     */
    private static String upperFirstCase(String str) {
        if(str != null && !str.equals("")) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
            return str;
        } else {
            return "";
        }
    }

    /**
     * 下划线转驼峰  字段->属性
     * @param name
     * @return
     */
    public static String convertToPropertyName(String name) {
        name = name.toLowerCase();
        String[] str = name.split("\\_");

        for(int i = 0; i < str.length; ++i) {
            if(i == 0) {
                name = str[i];
            } else {
                name = name + upperFirstCase(str[i]);
            }
        }

        return name;
    }

    /**
     * 驼峰转下划线 属性->表字段
     * @param name
     * @return
     */
    public static String convertToDataBaseName(String name) {
        char[] arr = name.toCharArray();
        StringBuffer buff = new StringBuffer("");

        for(int i = 0; i < arr.length; ++i) {
            if(Character.isUpperCase(arr[i])) {
                buff.append("_");
            }

            buff.append(String.valueOf(arr[i]).toUpperCase());
        }

        return buff.toString();
    }

    /**
     * 获取数据库列属性
     * @param rs
     * @return
     */
    public static List<Column> getResultColumns(ResultSet rs) {
        try {
            List<Column> e = new ArrayList<Column>();
            ResultSetMetaData rsmd = rs.getMetaData();

            for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
                Column column = new Column();
                column.setName(rsmd.getColumnLabel(i));
                column.setDataType(rsmd.getColumnType(i));
                e.add(column);
            }

            return e;
        } catch (Exception e) {
            throw new CompactdbException(e.getMessage());
        }
    }

    public static String compileScript(String script, Map<String, Object> values, Dialect dialect) {
        try {
            Map<String,Object> map = new HashMap<String,Object>();
            if(values != null) {
                Iterator<String> it = values.keySet().iterator();

                while(it.hasNext()) {
                    String key = it.next();
                    Object obj = values.get(key);
                    map.put(key, new ObjectWrapper(obj, dialect));
                }
            }

            return compileString(script, map);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String compileString(String String, Map<String, Object> root) {
        try {
            Configuration e = new Configuration();
            e.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
            e.setTemplateLoader(new StringTemplateLoader(String));
            e.setDefaultEncoding("UTF-8");
            StringWriter writer = new StringWriter();

            String var6;
            try {
                Template template = e.getTemplate("");
                template.process(root, writer);
                var6 = writer.toString();
            } finally {
                writer.close();
            }

            return var6;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Map<String, Object> propertiesToMap(Properties properties) {
        Map<String,Object> values = new HashMap<String,Object>();
        Iterator<?> it = properties.keySet().iterator();

        while(it.hasNext()) {
            String key = (String)it.next();
            String value = properties.getProperty(key);
            values.put(key.replaceAll("\\.", "_"), value);
        }

        return values;
    }

    public static Date parseDate(String str) {
        String[] parsePatterns = new String[]{"yyyy-MM-dd HH:mm:ss.SSS", "yyyy/MM/dd HH:mm:ss.SSS", "MM/dd/yyyy HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "MM/dd/yyyy HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm", "MM/dd/yyyy HH:mm", "yyyy-MM-dd HH", "yyyy/MM/dd HH", "MM/dd/yyyy HH", "yyyy-MM-dd", "yyyy/MM/dd", "MM/dd/yyyy"};
        SimpleDateFormat parser = null;
        ParsePosition pos = new ParsePosition(0);

        for(int i = 0; i < parsePatterns.length; ++i) {
            if(i == 0) {
                parser = new SimpleDateFormat(parsePatterns[0]);
            } else {
                parser.applyPattern(parsePatterns[i]);
            }

            pos.setIndex(0);
            Date date = parser.parse(str, pos);
            if(date != null && pos.getIndex() == str.length()) {
                return date;
            }
        }

        return null;
    }
}
