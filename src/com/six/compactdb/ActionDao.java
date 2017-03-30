package com.six.compactdb;

import java.util.List;

import com.six.compactdb.exception.CompactdbException;

public abstract interface ActionDao
{
  public abstract Object create(String paramString, DataValue paramDataValue)
    throws CompactdbException;

  public abstract void create(String paramString, List<DataValue> paramList)
    throws CompactdbException;

  public abstract void update(String paramString, DataValue paramDataValue)
    throws CompactdbException;

  public abstract void update(String paramString, List<DataValue> paramList)
    throws CompactdbException;

  public abstract void delete(String paramString, DataValue paramDataValue)
    throws CompactdbException;

  public abstract void delete(String paramString, List<DataValue> paramList)
    throws CompactdbException;

  public abstract void delete(String paramString1, String paramString2)
    throws CompactdbException;

  public abstract void delete(String paramString, String[] paramArrayOfString)
    throws CompactdbException;

  public abstract DataValue findById(String paramString, DataValue paramDataValue)
    throws CompactdbException;

  public abstract QueryResult findByIds(String paramString, List<DataValue> paramList)
    throws CompactdbException;

  public abstract DataValue findById(String paramString1, String paramString2)
    throws CompactdbException;

  public abstract QueryResult findByIds(String paramString, String[] paramArrayOfString)
    throws CompactdbException;

  public abstract QueryResult findAll(String paramString)
    throws CompactdbException;

  public abstract QueryPage findAll(String paramString, int paramInt1, int paramInt2)
    throws CompactdbException;

  public abstract int execute(String paramString, List<DataValue> paramList)
    throws CompactdbException;

  public abstract int execute(String paramString, List<DataValue> paramList, boolean paramBoolean)
    throws CompactdbException;

  public abstract int execute(String paramString, DataValue paramDataValue)
    throws CompactdbException;

  public abstract CallResult executeCall(String paramString, DataValue paramDataValue)
    throws CompactdbException;

  public abstract QueryResult executeQuery(String paramString, DataValue paramDataValue)
    throws CompactdbException;

  public abstract QueryPage executeQuery(String paramString, DataValue paramDataValue, int paramInt1, int paramInt2)
    throws CompactdbException;

  public abstract int executeSQL(String paramString)
    throws CompactdbException;

  public abstract QueryResult executeSQLQuery(String paramString)
    throws CompactdbException;

  public abstract QueryPage executeSQLQuery(String paramString, int paramInt1, int paramInt2)
    throws CompactdbException;
}
