package com.six.compactdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.six.compactdb.exception.CompactdbException;
import com.six.compactdb.model.Column;
import com.six.compactdb.util.EnergydbActionUtil;

public class QueryResult implements Iterator<DataValue>, List<DataValue> {
    Object lock = new Object();
    boolean nexted = false;
    boolean hasNext = false;
    ResultSet resultSet = null;
    List<DataValue> dataList = null;
    List<Column> columns = null;

    public QueryResult(ResultSet resultSet) {
        this.resultSet = resultSet;
        this.columns = EnergydbActionUtil.getResultColumns(resultSet);
    }

    public boolean hasNext() {
        if(this.resultSet == null) {
            return false;
        } else {
            synchronized(this.lock) {
                if(!this.nexted) {
                    try {
                        this.hasNext = this.resultSet.next();
                        this.nexted = true;
                    } catch (SQLException var3) {
                        throw new CompactdbException(var3);
                    }
                }

                return this.hasNext;
            }
        }
    }

    public DataValue next() {
        synchronized(this.lock) {
            DataValue var3;
            try {
                if(!this.hasNext()) {
                    this.close();
                    return null;
                }

                var3 = EnergydbActionUtil.parseResult(this.resultSet, this.columns);
            } finally {
                this.nexted = false;
            }

            return var3;
        }
    }

    public void remove() {
        synchronized(this.lock) {
            this.nexted = false;
        }
    }

    private void initList() {
        if(this.dataList == null) {
            this.dataList = EnergydbActionUtil.parseResultSet(this.resultSet);
            if(this.dataList == null) {
                this.dataList = new ArrayList<DataValue>();
            }

            this.close();
        }

    }

    public boolean add(DataValue e) {
        this.initList();
        return this.dataList.add(e);
    }

    public void add(int index, DataValue element) {
        this.initList();
        this.dataList.add(index, element);
    }

    public boolean addAll(Collection<? extends DataValue> c) {
        this.initList();
        return this.dataList.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends DataValue> c) {
        this.initList();
        return this.dataList.addAll(index, c);
    }

    public void clear() {
        this.initList();
        this.dataList.clear();
    }

    public boolean contains(Object o) {
        this.initList();
        return this.dataList.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        this.initList();
        return this.dataList.containsAll(c);
    }

    public DataValue get(int index) {
        this.initList();
        return (DataValue)this.dataList.get(index);
    }

    public int indexOf(Object o) {
        this.initList();
        return this.dataList.indexOf(o);
    }

    public boolean isEmpty() {
        this.initList();
        return this.dataList.isEmpty();
    }

    public Iterator<DataValue> iterator() {
        this.initList();
        return this.dataList.iterator();
    }

    public int lastIndexOf(Object o) {
        this.initList();
        return this.dataList.lastIndexOf(o);
    }

    public ListIterator<DataValue> listIterator() {
        this.initList();
        return this.dataList.listIterator();
    }

    public ListIterator<DataValue> listIterator(int index) {
        this.initList();
        return this.dataList.listIterator(index);
    }

    public boolean remove(Object o) {
        this.initList();
        return this.dataList.remove(o);
    }

    public DataValue remove(int index) {
        this.initList();
        return (DataValue)this.dataList.remove(index);
    }

    public boolean removeAll(Collection<?> c) {
        this.initList();
        return this.dataList.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        this.initList();
        return this.dataList.retainAll(c);
    }

    public DataValue set(int index, DataValue element) {
        this.initList();
        return (DataValue)this.dataList.set(index, element);
    }

    public int size() {
        this.initList();
        return this.dataList.size();
    }

    public List<DataValue> subList(int fromIndex, int toIndex) {
        this.initList();
        return this.dataList.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        this.initList();
        return this.dataList.toArray();
    }

    public <T> T[] toArray(T[] a) {
        this.initList();
        return this.dataList.toArray(a);
    }

    public void close() {
        try {
            this.resultSet.close();
        } catch (SQLException var3) {
            ;
        }

        try {
            this.resultSet.getStatement().close();
        } catch (SQLException var2) {
            ;
        }

    }
}
