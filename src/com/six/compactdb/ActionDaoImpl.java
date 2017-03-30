package com.six.compactdb;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.six.compactdb.ActionDao;
import com.six.compactdb.CallResult;
import com.six.compactdb.DataValue;
import com.six.compactdb.EnergySession;
import com.six.compactdb.QueryPage;
import com.six.compactdb.QueryResult;
import com.six.compactdb.exception.CompactdbException;
import com.six.compactdb.model.Column;
import com.six.compactdb.model.SQLParameter;
import com.six.compactdb.model.SQLScript;
import com.six.compactdb.model.Table;
import com.six.compactdb.util.EnergydbActionUtil;
import com.six.compactdb.util.UUIDGenerate;


public class ActionDaoImpl implements ActionDao {
    private static final Log log = LogFactory.getLog(ActionDaoImpl.class);
    private EnergySession energySession = null;
    private boolean isShowSql = false;

    public ActionDaoImpl(EnergySession energySession) {
        this.energySession = energySession;
        this.isShowSql = energySession.getConfiguration().getEnergyConfiguration().isShowSql();
    }

    public Object create(String tableId, DataValue value) throws CompactdbException {
        if(value == null) {
            throw new CompactdbException("没有指定要保存的记录");
        } else {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = this.energySession.getConnection();
                Table e = this.energySession.getConfiguration().getTableMapping().getTable(tableId);
                String sql = e.getInsertSql();
                String dataValue;
                if(e.getKeys() != null && e.getKeys().size() == 1) {
                    String index;
                    if(e.getKeyGenerate() != 2) {
                        if(e.getKeyGenerate() == 3) {
                            index = ((Column)e.getKeys().get(0)).getPropertyName();
                            value.setValue(index, UUIDGenerate.getUUID());
                        }
                    } else {
                        index = ((Column)e.getKeys().get(0)).getPropertyName();
                        dataValue = "SELECT " + e.getSequence() + ".Nextval FROM DUAL";
                        if(this.isShowSql) {
                            log.info(dataValue);
                        }

                        Statement i = null;
                        ResultSet column = null;

                        try {
                            i = conn.createStatement();
                            column = i.executeQuery(dataValue);
                            if(column.next()) {
                                value.setValue(index, Long.valueOf(column.getLong(1)));
                            }
                        } finally {
                            if(column != null) {
                                try {
                                    column.close();
                                } catch (SQLException var37) {
                                    ;
                                }
                            }

                            if(i != null) {
                                try {
                                    i.close();
                                } catch (SQLException var36) {
                                    ;
                                }
                            }

                        }

                        sql = sql.replaceAll(e.getSequence() + ".Nextval", "?");
                    }
                }

                if(this.isShowSql) {
                    log.info(sql);
                }

                pstmt = conn.prepareStatement(sql);
                int var41 = 0;

                for(int var42 = 0; var42 < e.getColumns().size(); ++var42) {
                    Column var44 = (Column)e.getColumns().get(var42);
                    ++var41;
                    this.setValue(pstmt, var41, var44.getDataType(), var44.getPropertyName(), value);
                }

                pstmt.executeUpdate();
                if(e.getKeys() == null) {
                    return null;
                } else if(e.getKeys().size() == 1) {
                    dataValue = ((Column)e.getKeys().get(0)).getPropertyName();
                    Object var47 = value.getObject(dataValue);
                    return var47;
                } else {
                    DataValue var43 = new DataValue();

                    for(int var45 = 0; var45 < e.getKeys().size(); ++var45) {
                        Column var46 = (Column)e.getKeys().get(var45);
                        var43.setValue(var46.getPropertyName(), value.getObject(var46.getPropertyName()));
                    }

                    DataValue var14 = var43;
                    return var14;
                }
            } catch (SQLException var39) {
                throw new CompactdbException(var39);
            } finally {
                if(pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException var35) {
                        ;
                    }
                }

            }
        }
    }

    public void create(String tableId, List<DataValue> values) throws CompactdbException {
        if(values == null) {
            throw new CompactdbException("没有指定要保存的记录");
        } else {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = this.energySession.getConnection();
                int e = this.energySession.getConfiguration().getDataBaseConfig().getBatchSize();
                Table table = this.energySession.getConfiguration().getTableMapping().getTable(tableId);
                String sql = table.getInsertSql();
                if(this.isShowSql) {
                    log.info(sql);
                }

                pstmt = conn.prepareStatement(sql);
                int r = 0;

                while(true) {
                    if(r >= values.size()) {
                        if(values.size() % e != 0) {
                            pstmt.executeBatch();
                        }
                        break;
                    }

                    DataValue value = (DataValue)values.get(r);
                    int index = 0;

                    for(int i = 0; i < table.getColumns().size(); ++i) {
                        Column column = (Column)table.getColumns().get(i);
                        if(column.isPrimaryKey()) {
                            if(table.getKeyGenerate() == 0) {
                                ++index;
                                this.setValue(pstmt, index, column.getDataType(), column.getPropertyName(), value);
                            } else if(table.getKeyGenerate() == 3) {
                                ++index;
                                pstmt.setString(index, UUIDGenerate.getUUID());
                            }
                        } else {
                            ++index;
                            this.setValue(pstmt, index, column.getDataType(), column.getPropertyName(), value);
                        }
                    }

                    pstmt.addBatch();
                    if((r + 1) % e == 0) {
                        pstmt.executeBatch();
                    }

                    ++r;
                }
            } catch (SQLException var20) {
                throw new CompactdbException(var20);
            } finally {
                if(pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException var19) {
                        ;
                    }
                }

            }

        }
    }

    public void update(String tableId, DataValue value) throws CompactdbException {
        if(value == null) {
            throw new CompactdbException("没有指定要更新的记录");
        } else {
            List<DataValue> values = new ArrayList<DataValue>();
            values.add(value);
            this.update(tableId, values);
        }
    }

    public void update(String tableId, List<DataValue> values) throws CompactdbException {
        if(values == null) {
            throw new CompactdbException("没有指定要更新的记录");
        } else {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = this.energySession.getConnection();
                int e = this.energySession.getConfiguration().getDataBaseConfig().getBatchSize();
                Table table = this.energySession.getConfiguration().getTableMapping().getTable(tableId);
                if(!table.existsPrimaryKey()) {
                    throw new CompactdbException("表\"" + table.getName() + "\"没有主健，不能根据主健进行更新");
                }

                String sql = table.getUpdateSql((DataValue)values.get(0));
                if(this.isShowSql) {
                    log.info(sql);
                }

                pstmt = conn.prepareStatement(sql);
                int r = 0;

                while(true) {
                    if(r >= values.size()) {
                        if(values.size() % e != 0) {
                            pstmt.executeBatch();
                        }
                        break;
                    }

                    DataValue value = (DataValue)values.get(r);
                    int index = 0;
                    String key_str = "";

                    for(int key_set = 0; key_set < table.getKeys().size(); ++key_set) {
                        Column i = (Column)table.getKeys().get(key_set);
                        key_str = key_str + i.getName();
                    }

                    Set<?> var26 = value.keySet();
                    Iterator<?> var27 = var26.iterator();

                    while(var27.hasNext()) {
                        String column = (String)var27.next();
                        String db_col = EnergydbActionUtil.convertToDataBaseName(column);
                        if(key_str.indexOf(db_col) < 0) {
                            ++index;
                            Column col = table.getColumn(db_col);
                            this.setValue(pstmt, index, col.getDataType(), column, value);
                        }
                    }

                    for(int var28 = 0; var28 < table.getKeys().size(); ++var28) {
                        Column var29 = (Column)table.getKeys().get(var28);
                        ++index;
                        this.setValue(pstmt, index, var29.getDataType(), var29.getPropertyName(), value);
                    }

                    pstmt.addBatch();
                    if((r + 1) % e == 0) {
                        pstmt.executeBatch();
                    }

                    ++r;
                }
            } catch (SQLException var24) {
                throw new CompactdbException(var24);
            } finally {
                if(pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException var23) {
                        ;
                    }
                }

            }

        }
    }

    public void delete(String tableId, DataValue value) throws CompactdbException {
        if(value == null) {
            throw new CompactdbException("没有指定要删除的记录");
        } else {
            List<DataValue> values = new ArrayList<DataValue>();
            values.add(value);
            this.delete(tableId, values);
        }
    }

    public void delete(String tableId, List<DataValue> values) throws CompactdbException {
        if(values == null) {
            throw new CompactdbException("没有指定要删除的记录");
        } else {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = this.energySession.getConnection();
                int e = this.energySession.getConfiguration().getDataBaseConfig().getBatchSize();
                Table table = this.energySession.getConfiguration().getTableMapping().getTable(tableId);
                if(!table.existsPrimaryKey()) {
                    throw new CompactdbException("表\"" + table.getName() + "\"没有主健，不能根据主健进行删除");
                }

                String sql = table.getDeleteByKeySql();
                if(this.isShowSql) {
                    log.info(sql);
                }

                pstmt = conn.prepareStatement(sql);

                for(int r = 0; r < values.size(); ++r) {
                    DataValue value = (DataValue)values.get(r);
                    int index = 0;

                    for(int i = 0; i < table.getKeys().size(); ++i) {
                        Column column = (Column)table.getKeys().get(i);
                        ++index;
                        this.setValue(pstmt, index, column.getDataType(), column.getPropertyName(), value);
                    }

                    pstmt.addBatch();
                    if((r + 1) % e == 0) {
                        pstmt.executeBatch();
                    }
                }

                if(values.size() % e != 0) {
                    pstmt.executeBatch();
                }
            } catch (SQLException var20) {
                throw new CompactdbException(var20);
            } finally {
                if(pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException var19) {
                        ;
                    }
                }

            }

        }
    }

    public void delete(String tableId, String id) throws CompactdbException {
        if(id == null) {
            throw new CompactdbException("没有指定要删除的记录");
        } else {
            this.delete(tableId, new String[]{id});
        }
    }

    public void delete(String tableId, String[] ids) throws CompactdbException {
        if(ids != null && ids.length != 0) {
            Table table = this.energySession.getConfiguration().getTableMapping().getTable(tableId);
            if(!table.existsPrimaryKey()) {
                throw new CompactdbException("表\"" + table.getName() + "\"没有主健，不能根据主健进行删除");
            } else {
            	List<DataValue> values = new ArrayList<DataValue>();
                Column column = (Column)table.getKeys().get(0);

                for(int i = 0; i < ids.length; ++i) {
                    DataValue value = new DataValue();
                    value.put(column.getPropertyName(), ids[i]);
                    values.add(value);
                }

                this.delete(tableId, values);
            }
        } else {
            throw new CompactdbException("没有指定要删除的记录");
        }
    }

    public DataValue findById(String tableId, DataValue value) throws CompactdbException {
        if(value == null) {
            throw new CompactdbException("没有指定要查询的记录");
        } else {
        	List<DataValue> values = new ArrayList<DataValue>();
            values.add(value);
            QueryResult dataList = this.findByIds(tableId, values);
            return dataList != null && dataList.size() > 0?(DataValue)dataList.get(0):null;
        }
    }

    public QueryResult findByIds(String tableId, List<DataValue> values) throws CompactdbException {
        if(values == null) {
            throw new CompactdbException("没有指定要查询的记录");
        } else {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                conn = this.energySession.getConnection();
                Table e = this.energySession.getConfiguration().getTableMapping().getTable(tableId);
                if(!e.existsPrimaryKey()) {
                    throw new CompactdbException("表\"" + e.getName() + "\"没有主健，不能根据主健进行查询");
                } else {
                    StringBuffer sql = new StringBuffer();
                    String findSql = e.getFindByKeySql();
                    int index;
                    if(values.size() == 1) {
                        sql.append(findSql);
                    } else {
                        sql.append("SELECT * FROM (");

                        for(index = 0; index < values.size(); ++index) {
                            if(index == 0) {
                                sql.append(findSql);
                            } else {
                                sql.append(" UNION ").append(findSql);
                            }
                        }

                        sql.append(") T");
                    }

                    if(this.isShowSql) {
                        log.info(sql.toString());
                    }

                    pstmt = conn.prepareStatement(sql.toString());
                    index = 0;

                    for(int r = 0; r < values.size(); ++r) {
                        DataValue value = (DataValue)values.get(r);

                        for(int i = 0; i < e.getKeys().size(); ++i) {
                            Column column = (Column)e.getKeys().get(i);
                            ++index;
                            this.setValue(pstmt, index, column.getDataType(), column.getPropertyName(), value);
                        }
                    }

                    rs = pstmt.executeQuery();
                    QueryResult var15 = new QueryResult(rs);
                    return var15;
                }
            } catch (SQLException var18) {
                throw new CompactdbException(var18);
            } finally {
                this.energySession.addClears(rs);
                this.energySession.addClears(pstmt);
            }
        }
    }

    public DataValue findById(String tableId, String id) throws CompactdbException {
        if(id == null) {
            throw new CompactdbException("没有指定要删除的记录");
        } else {
            QueryResult dataList = this.findByIds(tableId, new String[]{id});
            return dataList != null && dataList.size() > 0?(DataValue)dataList.get(0):null;
        }
    }

    public QueryResult findByIds(String tableId, String[] ids) throws CompactdbException {
        if(ids != null && ids.length != 0) {
            Table table = this.energySession.getConfiguration().getTableMapping().getTable(tableId);
            if(!table.existsPrimaryKey()) {
                throw new CompactdbException("表\"" + table.getName() + "\"没有主健，不能根据主健进行查询");
            } else {
            	List<DataValue> values = new ArrayList<DataValue>();
                Column column = (Column)table.getKeys().get(0);

                for(int i = 0; i < ids.length; ++i) {
                    DataValue value = new DataValue();
                    value.put(column.getPropertyName(), ids[i]);
                    values.add(value);
                }

                return this.findByIds(tableId, values);
            }
        } else {
            throw new CompactdbException("没有指定要查询的记录");
        }
    }

    public QueryResult findAll(String tableId) throws CompactdbException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        QueryResult var8;
        try {
            conn = this.energySession.getConnection();
            Table e = this.energySession.getConfiguration().getTableMapping().getTable(tableId);
            String sql = e.getFindAllSql();
            if(this.isShowSql) {
                log.info(sql);
            }

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            var8 = new QueryResult(rs);
        } catch (SQLException var11) {
            throw new CompactdbException(var11);
        } finally {
            this.energySession.addClears(rs);
            this.energySession.addClears(stmt);
        }

        return var8;
    }

    public QueryPage findAll(String tableId, int currentPage, int pageSize) throws CompactdbException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        QueryPage var16;
        try {
            if(currentPage < 1) {
                currentPage = 1;
            }

            int e = (currentPage - 1) * pageSize + 1;
            int max = currentPage * pageSize;
            conn = this.energySession.getConnection();
            Table table = this.energySession.getConfiguration().getTableMapping().getTable(tableId);
            String sql = table.getFindAllSql();
            String countSql = "SELECT COUNT(1) FROM (" + sql + ") T";
            String limitSql = this.energySession.getConfiguration().getDataBaseConfig().getDialect().getLimitQueryScript(sql, e, max);
            if(this.isShowSql) {
                log.info(countSql);
            }

            stmt = conn.createStatement();
            rs = stmt.executeQuery(countSql);
            int totalCount = 0;
            if(rs.next()) {
                totalCount = rs.getInt(1);
            }

            rs.close();
            stmt.close();
            stmt = null;
            QueryResult resultList = null;
            if(totalCount > 0) {
                if(this.isShowSql) {
                    log.info(limitSql);
                }

                stmt = conn.createStatement();
                rs = stmt.executeQuery(limitSql);
                resultList = new QueryResult(rs);
            }

            var16 = new QueryPage(resultList, totalCount, currentPage, pageSize);
        } catch (SQLException var19) {
            throw new CompactdbException(var19);
        } finally {
            this.energySession.addClears(rs);
            this.energySession.addClears(stmt);
        }

        return var16;
    }

    public int execute(String queryId, List<DataValue> values) throws CompactdbException {
        return this.execute(queryId, values, false);
    }

    public int execute(String queryId, List<DataValue> values, boolean removeEffectData) throws CompactdbException {
        if(values != null && values.size() != 0) {
            int effectRow = 0;
            SQLScript script = this.energySession.getConfiguration().getSQLScriptMapping().getSQLScript(queryId);
            Connection conn;
            int e;
            int[] ret1;
            int m1;
            int var15;
            int var41;
            if(script.getParameters() != null && script.getParameters().size() != 0) {
                conn = null;
                PreparedStatement var38 = null;

                try {
                    conn = this.energySession.getConnection();
                    e = this.energySession.getConfiguration().getDataBaseConfig().getBatchSize();
                    String var40 = script.getSql();
                    if(this.isShowSql) {
                        log.info(var40);
                    }

                    var38 = conn.prepareStatement(var40);

                    for(var41 = values.size() - 1; var41 >= 0; --var41) {
                        DataValue var42 = (DataValue)values.get(var41);

                        for(int var45 = 0; script.getParameters() != null && var45 < script.getParameters().size(); ++var45) {
                            SQLParameter var46 = (SQLParameter)script.getParameters().get(var45);
                            if(!var46.getType().equals("OUT")) {
                                this.setValue(var38, var45 + 1, var46.getDataType(), var46.getName(), var42);
                            }
                        }

                        var38.addBatch();
                        if((var41 + 1) % e == 0) {
                            ret1 = var38.executeBatch();

                            for(m1 = 0; m1 < ret1.length; ++m1) {
                                if(ret1[m1] > 0) {
                                    if(removeEffectData) {
                                        values.remove(ret1.length - m1 - 1 + var41);
                                    }

                                    ++effectRow;
                                } else if(ret1[m1] == -2) {
                                    effectRow = -2;
                                }
                            }
                        }
                    }

                    if(values.size() % e != 0) {
                        int[] var43 = var38.executeBatch();

                        for(int var44 = 0; var44 < var43.length; ++var44) {
                            if(var43[var44] > 0) {
                                if(removeEffectData) {
                                    values.remove(var43.length - var44 - 1);
                                }

                                ++effectRow;
                            } else if(var43[var44] == -2) {
                                effectRow = -2;
                            }
                        }
                    }

                    var15 = effectRow;
                    return var15;
                } catch (SQLException var34) {
                    throw new CompactdbException(var34);
                } finally {
                    if(var38 != null) {
                        try {
                            var38.close();
                        } catch (SQLException var33) {
                            ;
                        }
                    }

                }
            } else {
                conn = null;
                Statement pstmt = null;

                try {
                    conn = this.energySession.getConnection();
                    e = this.energySession.getConfiguration().getDataBaseConfig().getBatchSize();
                    pstmt = conn.createStatement();

                    for(int sql = values.size() - 1; sql >= 0; --sql) {
                        DataValue ret = (DataValue)values.get(sql);
                        String m = EnergydbActionUtil.compileScript(script.getSql(), ret == null?null:ret, this.energySession.getConfiguration().getDataBaseConfig().getDialect());
                        if(this.isShowSql) {
                            log.info(m);
                        }

                        pstmt.addBatch(m);
                        if((sql + 1) % e == 0) {
                            ret1 = pstmt.executeBatch();

                            for(m1 = 0; m1 < ret1.length; ++m1) {
                                if(ret1[m1] > 0) {
                                    if(removeEffectData) {
                                        values.remove(ret1.length - m1 - 1 + sql);
                                    }

                                    ++effectRow;
                                } else if(ret1[m1] == -2) {
                                    effectRow = -2;
                                }
                            }
                        }
                    }

                    if(values.size() % e != 0) {
                        int[] var39 = pstmt.executeBatch();

                        for(var41 = 0; var41 < var39.length; ++var41) {
                            if(var39[var41] > 0) {
                                if(removeEffectData) {
                                    values.remove(var39.length - var41 - 1);
                                }

                                ++effectRow;
                            } else if(var39[var41] == -2) {
                                effectRow = -2;
                            }
                        }
                    }

                    var15 = effectRow;
                } catch (SQLException var36) {
                    throw new CompactdbException(var36);
                } finally {
                    if(pstmt != null) {
                        try {
                            pstmt.close();
                        } catch (SQLException var32) {
                            ;
                        }
                    }

                }

                return var15;
            }
        } else {
            return 0;
        }
    }

    public int execute(String queryId, DataValue value) throws CompactdbException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = this.energySession.getConnection();
            SQLScript e = this.energySession.getConfiguration().getSQLScriptMapping().getSQLScript(queryId);
            String sql = EnergydbActionUtil.compileScript(e.getSql(), value == null?null:value, this.energySession.getConfiguration().getDataBaseConfig().getDialect());
            if(this.isShowSql) {
                log.info(sql);
            }

            pstmt = conn.prepareStatement(sql);

            for(int i = 0; e.getParameters() != null && i < e.getParameters().size(); ++i) {
                SQLParameter parameter = (SQLParameter)e.getParameters().get(i);
                if(!parameter.getType().equals("OUT")) {
                    this.setValue(pstmt, i + 1, parameter.getDataType(), parameter.getName(), value);
                }
            }

            pstmt.execute();
            int var10 = pstmt.getUpdateCount();
            return var10;
        } catch (SQLException var17) {
            throw new CompactdbException(var17);
        } finally {
            if(pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException var16) {
                    ;
                }
            }

        }
    }

    public CallResult executeCall(String queryId, DataValue value) throws CompactdbException {
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;

        CallResult var16;
        try {
            conn = this.energySession.getConnection();
            SQLScript e = this.energySession.getConfiguration().getSQLScriptMapping().getSQLScript(queryId);
            String sql = EnergydbActionUtil.compileScript(e.getSql(), value == null?null:value, this.energySession.getConfiguration().getDataBaseConfig().getDialect());
            if(this.isShowSql) {
                log.info(sql);
            }

            long l = System.currentTimeMillis();
            cstmt = conn.prepareCall(sql);

            for(int values = 0; e.getParameters() != null && values < e.getParameters().size(); ++values) {
                SQLParameter lastResult = (SQLParameter)e.getParameters().get(values);
                if(lastResult.getType().equals("OUT")) {
                    cstmt.registerOutParameter(values + 1, lastResult.getDataType());
                } else {
                    this.setValue(cstmt, values + 1, lastResult.getDataType(), lastResult.getName(), value);
                }
            }

            DataValue var21 = null;
            QueryResult var22 = null;
            if(e.isQuery()) {
                rs = cstmt.executeQuery();
                var22 = new QueryResult(rs);
            } else {
                cstmt.execute();

                for(int i = 0; e.getParameters() != null && i < e.getParameters().size(); ++i) {
                    SQLParameter parameter = (SQLParameter)e.getParameters().get(i);
                    if(parameter.getType().equals("OUT")) {
                        Object object = cstmt.getObject(i + 1);
                        if(var21 == null) {
                            var21 = new DataValue();
                        }

                        var21.setValue(parameter.getName(), object);
                        if(object instanceof ResultSet) {
                            this.energySession.addClears(object);
                        }
                    }
                }
            }

            if(this.isShowSql) {
                log.info("time:" + (System.currentTimeMillis() - l) + "ms");
            }

            var16 = new CallResult(var22, var21);
        } catch (SQLException var19) {
            throw new CompactdbException(var19);
        } finally {
            this.energySession.addClears(rs);
            this.energySession.addClears(cstmt);
        }

        return var16;
    }

    public QueryResult executeQuery(String queryId, DataValue value) throws CompactdbException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = this.energySession.getConnection();
            SQLScript e = this.energySession.getConfiguration().getSQLScriptMapping().getSQLScript(queryId);
            String sql = EnergydbActionUtil.compileScript(e.getSql(), value == null?null:value, this.energySession.getConfiguration().getDataBaseConfig().getDialect());
            if(this.isShowSql) {
                log.info(sql);
            }

            long l = System.currentTimeMillis();
            pstmt = conn.prepareStatement(sql);

            for(int i = 0; e.getParameters() != null && i < e.getParameters().size(); ++i) {
                SQLParameter parameter = (SQLParameter)e.getParameters().get(i);
                if(!parameter.getType().equals("OUT")) {
                    this.setValue(pstmt, i + 1, parameter.getDataType(), parameter.getName(), value);
                }
            }

            rs = pstmt.executeQuery();
            if(this.isShowSql) {
                log.info("time:" + (System.currentTimeMillis() - l) + "ms");
            }

            QueryResult var13 = new QueryResult(rs);
            return var13;
        } catch (SQLException var16) {
            throw new CompactdbException(var16);
        } finally {
            this.energySession.addClears(rs);
            this.energySession.addClears(pstmt);
        }
    }

    public QueryPage executeQuery(String queryId, DataValue value, int currentPage, int pageSize) throws CompactdbException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        QueryPage var21;
        try {
            if(currentPage < 1) {
                currentPage = 1;
            }

            int e = (currentPage - 1) * pageSize + 1;
            int max = currentPage * pageSize;
            conn = this.energySession.getConnection();
            SQLScript script = this.energySession.getConfiguration().getSQLScriptMapping().getSQLScript(queryId);
            String sql = EnergydbActionUtil.compileScript(script.getSql(), value == null?null:value, this.energySession.getConfiguration().getDataBaseConfig().getDialect());
            String countSql = "SELECT COUNT(1) FROM (" + sql + ") T";
            if(this.isShowSql) {
                log.info(countSql);
            }

            long l = System.currentTimeMillis();
            pstmt = conn.prepareStatement(countSql);

            int totalCount;
            for(totalCount = 0; script.getParameters() != null && totalCount < script.getParameters().size(); ++totalCount) {
                SQLParameter resultList = (SQLParameter)script.getParameters().get(totalCount);
                if(!resultList.getType().equals("OUT")) {
                    this.setValue(pstmt, totalCount + 1, resultList.getDataType(), resultList.getName(), value);
                }
            }

            rs = pstmt.executeQuery();
            totalCount = 0;
            if(rs.next()) {
                totalCount = rs.getInt(1);
            }

            rs.close();
            pstmt.close();
            QueryResult var26 = null;
            if(totalCount > 0) {
                if(totalCount < e) {
                    currentPage = 1;
                    e = 1;
                    max = pageSize;
                }

                String limitSql = this.energySession.getConfiguration().getDataBaseConfig().getDialect().getLimitQueryScript(sql, e, max);
                if(this.isShowSql) {
                    log.info(limitSql);
                }

                pstmt = conn.prepareStatement(limitSql);

                for(int i = 0; script.getParameters() != null && i < script.getParameters().size(); ++i) {
                    SQLParameter parameter = (SQLParameter)script.getParameters().get(i);
                    if(!parameter.getType().equals("OUT")) {
                        this.setValue(pstmt, i + 1, parameter.getDataType(), parameter.getName(), value);
                    }
                }

                rs = pstmt.executeQuery();
                var26 = new QueryResult(rs);
            }

            if(this.isShowSql) {
                log.info("time:" + (System.currentTimeMillis() - l) + "ms");
            }

            var21 = new QueryPage(var26, totalCount, currentPage, pageSize);
        } catch (SQLException var24) {
            throw new CompactdbException(var24);
        } finally {
            this.energySession.addClears(rs);
            this.energySession.addClears(pstmt);
        }

        return var21;
    }

    private void setValue(PreparedStatement pstmt, int index, int dataType, String name, DataValue value) throws SQLException {
        if(dataType != 93 && dataType != 91) {
            if(dataType == -5) {
                pstmt.setLong(index, value.getLong(name).longValue());
            } else if(dataType != 4 && dataType != 5 && dataType != -6 && dataType != -7) {
                if(dataType != 3 && dataType != 2 && dataType != 8 && dataType != 6) {
                    if(dataType != -2 && dataType != 2004 && dataType != -3 && dataType != 2000) {
                        pstmt.setString(index, value.getString(name));
                    } else {
                        pstmt.setBytes(index, value.getBytes(name));
                    }
                } else {
                    pstmt.setBigDecimal(index, value.getBigDecimal(name));
                }
            } else {
                pstmt.setInt(index, value.getInt(name));
            }
        } else {
            pstmt.setTimestamp(index, value.getTimestamp(name));
        }

    }

    public int executeSQL(String sql) throws CompactdbException {
        Connection conn = null;
        Statement stmt = null;

        int var6;
        try {
            conn = this.energySession.getConnection();
            if(this.isShowSql) {
                log.info(sql);
            }

            stmt = conn.createStatement();
            stmt.execute(sql);
            var6 = stmt.getUpdateCount();
        } catch (SQLException var13) {
            throw new CompactdbException(var13);
        } finally {
            if(stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException var12) {
                    ;
                }
            }

        }

        return var6;
    }

    public QueryResult executeSQLQuery(String sql) throws CompactdbException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        QueryResult var7;
        try {
            conn = this.energySession.getConnection();
            if(this.isShowSql) {
                log.info(sql);
            }
 
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            var7 = new QueryResult(rs);
        } catch (SQLException var10) {
            throw new CompactdbException(var10);
        } finally {
            this.energySession.addClears(rs);
            this.energySession.addClears(stmt);
        }

        return var7;
    }

    public QueryPage executeSQLQuery(String sql, int currentPage, int pageSize) throws CompactdbException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        QueryPage var14;
        try {
            if(currentPage < 1) {
                currentPage = 1;
            }

            int e = (currentPage - 1) * pageSize + 1;
            int max = currentPage * pageSize;
            conn = this.energySession.getConnection();
            String countSql = "SELECT COUNT(1) FROM (" + sql + ") T";
            String limitSql = this.energySession.getConfiguration().getDataBaseConfig().getDialect().getLimitQueryScript(sql, e, max);
            if(this.isShowSql) {
                log.info(countSql);
            }

            stmt = conn.createStatement();
            rs = stmt.executeQuery(countSql);
            int totalCount = 0;
            if(rs.next()) {
                totalCount = rs.getInt(1);
            }

            rs.close();
            stmt.close();
            QueryResult resultList = null;
            if(totalCount > 0) {
                if(this.isShowSql) {
                    log.info(limitSql);
                }

                stmt = conn.createStatement();
                rs = stmt.executeQuery(limitSql);
                resultList = new QueryResult(rs);
            }

            var14 = new QueryPage(resultList, totalCount, currentPage, pageSize);
        } catch (SQLException var17) {
            throw new CompactdbException(var17);
        } finally {
            this.energySession.addClears(rs);
            this.energySession.addClears(stmt);
        }

        return var14;
    }
}
