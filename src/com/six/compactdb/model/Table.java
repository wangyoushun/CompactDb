package com.six.compactdb.model;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.six.compactdb.DataValue;
import com.six.compactdb.dialect.Dialect;
import com.six.compactdb.util.EnergydbActionUtil;

public class Table {
    public static final int ASSIGN = 0;
    public static final int IDENTITY = 1;
    public static final int SEQUENCE = 2;
    public static final int UUID = 3;
    private String name = null;
    private List<Column> keys = null;
    private List<Column> columns = null;
    private Dialect dialect = null;
    private String keyGenerate = null;
    private String sequence = null;
    private String insertSql = null;
    private String updateSql = null;
    private String deleteByKeySql = null;
    private String findByKeySql = null;
    private String findAllSql = null;

    public Table(String name, List<Column> keys, List<Column> columns, Dialect dialect, String keyGenerate, String sequence) {
        this.name = name;
        this.keys = keys;
        this.columns = columns;
        this.dialect = dialect;
        this.keyGenerate = keyGenerate;
        this.sequence = sequence;
        this.insertSql = this.getInsertSQL();
        this.updateSql = this.getUpdateSQL();
        this.deleteByKeySql = this.getDeleteByKeySQL();
        this.findByKeySql = this.getFindByKeySQL();
        this.findAllSql = this.getFindAllSQL();
    }

    public String getName() {
        return this.name;
    }

    public List<Column> getKeys() {
        return this.keys;
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public Column getColumn(String col) {
        Iterator<Column> iterator = this.columns.iterator();

        while(iterator.hasNext()) {
            Column cols = iterator.next();
            if(cols.getName().equals(col.toLowerCase())) {
                return cols;
            }
        }

        return null;
    }

    public Dialect getDialect() {
        return this.dialect;
    }

    public int getKeyGenerate() {
        return this.keyGenerate != null && !this.keyGenerate.equals("assign")?(this.keyGenerate.equals("identity")?1:(this.keyGenerate.equals("uuid")?3:(this.keyGenerate.equals("sequence")?2:0))):0;
    }

    public String getSequence() {
        return this.sequence;
    }

    public String getPropertyName() {
        return EnergydbActionUtil.convertToPropertyName(this.name);
    }

    public boolean existsPrimaryKey() {
        return this.keys != null && this.keys.size() > 0;
    }

    public boolean isCanUpdateByKeys() {
        return this.keys == null || this.columns == null || this.keys.size() != this.columns.size();
    }

    private String getInsertSQL() {
        StringBuffer sql = new StringBuffer();
        StringBuffer cs = new StringBuffer();
        StringBuffer vs = new StringBuffer();

        for(int i = 0; i < this.getColumns().size(); ++i) {
            Column column = (Column)this.getColumns().get(i);
            if(!column.isPrimaryKey() || column.isPrimaryKey() && this.getKeyGenerate() != 1) {
                cs.append(",").append(this.getDialect().getPrefixEsc()).append(column.getName()).append(this.getDialect().getSuffixEsc());
                if(column.isPrimaryKey() && this.getKeyGenerate() == 2) {
                    vs.append("," + this.getSequence() + ".Nextval");
                } else {
                    vs.append(",?");
                }
            }
        }

        return sql.append("INSERT INTO ").append(this.getName()).append("(").append(cs.toString().substring(1)).append(") ").append("VALUES(").append(vs.toString().substring(1)).append(")").toString();
    }

    private String getUpdateSQL() {
        StringBuffer set = new StringBuffer();
        StringBuffer where = new StringBuffer();

        int i;
        Column column;
        for(i = 0; this.getKeys() != null && i < this.getKeys().size(); ++i) {
            column = (Column)this.getKeys().get(i);
            if(where.length() == 0) {
                where.append(this.getDialect().getPrefixEsc()).append(column.getName()).append(this.getDialect().getSuffixEsc()).append(" = ? ");
            } else {
                where.append(" AND ").append(this.getDialect().getPrefixEsc()).append(column.getName()).append(this.getDialect().getSuffixEsc()).append(" = ? ");
            }
        }

        for(i = 0; this.getColumns() != null && i < this.getColumns().size(); ++i) {
            column = (Column)this.getColumns().get(i);
            if(!column.isPrimaryKey()) {
                if(set.length() == 0) {
                    set.append(this.getDialect().getPrefixEsc()).append(column.getName()).append(this.getDialect().getSuffixEsc()).append(" = ? ");
                } else {
                    set.append(" , ").append(this.getDialect().getPrefixEsc()).append(column.getName()).append(this.getDialect().getSuffixEsc()).append(" = ? ");
                }
            }
        }

        return (new StringBuffer()).append("UPDATE ").append(this.getName()).append(" SET ").append(set).append(" WHERE ").append(where).toString();
    }

    private String getUpdateSQL(DataValue dv) {
        StringBuffer set = new StringBuffer();
        StringBuffer where = new StringBuffer();
        String keys_str = "";

        for(int key_set = 0; this.getKeys() != null && key_set < this.getKeys().size(); ++key_set) {
            Column iterator = (Column)this.getKeys().get(key_set);
            keys_str = keys_str + iterator.getName();
            if(where.length() == 0) {
                where.append(this.getDialect().getPrefixEsc()).append(iterator.getName()).append(this.getDialect().getSuffixEsc()).append(" = ? ");
            } else {
                where.append(" AND ").append(this.getDialect().getPrefixEsc()).append(iterator.getName()).append(this.getDialect().getSuffixEsc()).append(" = ? ");
            }
        }

        Set<?> dvKey = dv.keySet();
        Iterator<?> dvData = dvKey.iterator();

        while(dvData.hasNext()) {
            String dv_key = (String)dvData.next();
            String db_col = EnergydbActionUtil.convertToDataBaseName(dv_key);
            if(keys_str.indexOf(db_col) < 0) {
                if(set.length() == 0) {
                    set.append(this.getDialect().getPrefixEsc()).append(db_col).append(this.getDialect().getSuffixEsc()).append(" = ? ");
                } else {
                    set.append(" , ").append(this.getDialect().getPrefixEsc()).append(db_col).append(this.getDialect().getSuffixEsc()).append(" = ? ");
                }
            }
        }

        return (new StringBuffer()).append("UPDATE ").append(this.getName()).append(" SET ").append(set).append(" WHERE ").append(where).toString();
    }

    private String getDeleteByKeySQL() {
        StringBuffer where = new StringBuffer();

        for(int i = 0; this.getKeys() != null && i < this.getKeys().size(); ++i) {
            Column column = (Column)this.getKeys().get(i);
            if(where.length() == 0) {
                where.append(this.getDialect().getPrefixEsc()).append(column.getName()).append(this.getDialect().getSuffixEsc()).append(" = ? ");
            } else {
                where.append(" AND ").append(this.getDialect().getPrefixEsc()).append(column.getName()).append(this.getDialect().getSuffixEsc()).append(" = ? ");
            }
        }

        return (new StringBuffer()).append("DELETE FROM ").append(this.getName()).append(" WHERE ").append(where).toString();
    }

    private String getFindByKeySQL() {
        StringBuffer where = new StringBuffer();

        for(int i = 0; this.getKeys() != null && i < this.getKeys().size(); ++i) {
            Column column = (Column)this.getKeys().get(i);
            if(where.length() == 0) {
                where.append(this.getDialect().getPrefixEsc()).append(column.getName()).append(this.getDialect().getSuffixEsc()).append(" = ? ");
            } else {
                where.append(" AND ").append(this.getDialect().getPrefixEsc()).append(column.getName()).append(this.getDialect().getSuffixEsc()).append(" = ? ");
            }
        }

        return (new StringBuffer()).append("SELECT * FROM ").append(this.getName()).append(" WHERE ").append(where).toString();
    }

    private String getFindAllSQL() {
        return "SELECT * FROM " + this.getName();
    }

    public String getInsertSql() {
        return this.insertSql;
    }

    public String getUpdateSql() {
        return this.updateSql;
    }

    public String getUpdateSql(DataValue dv) {
        return this.getUpdateSQL(dv);
    }

    public String getDeleteByKeySql() {
        return this.deleteByKeySql;
    }

    public String getFindByKeySql() {
        return this.findByKeySql;
    }

    public String getFindAllSql() {
        return this.findAllSql;
    }
}
