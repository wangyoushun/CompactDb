package com.six.test;

import org.junit.Test;

import com.six.compactdb.ActionDao;
import com.six.compactdb.CompactdbUtil;
import com.six.compactdb.DataValue;
import com.six.compactdb.QueryResult;

public class CrudTest {

	/**
	 * 通过xml配置文件查询 预编译
	 */
	@Test
	public void testQueryByXml2() {
		ActionDao dao = CompactdbUtil.currentActionDao();
		DataValue dv = new DataValue();
		dv.setValue("name", "叶小民16");
		QueryResult result = dao.executeQuery("user.querUser2", dv);
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	
	/**
	 * 通过xml配置文件查询
	 */
	@Test
	public void testQueryByXml() {
		ActionDao dao = CompactdbUtil.currentActionDao();
		DataValue dv = new DataValue();
		dv.setValue("name", "叶小民16");
		QueryResult result = dao.executeQuery("user.querUser", dv);
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	
	/**
	 * crud by key
	 */
	@Test
	public void testCrudByKey() {
		ActionDao dao = CompactdbUtil.currentActionDao();
		DataValue dv = new DataValue();
		insert(dao, dv);
		delete(dao, dv);
		update(dao, dv);
		findById(dao, dv);
		findAll(dao);
	}

	/**
	 * sqlQuery
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSqlQuery() throws Exception {
		String sql = "select * from user limit 10";
		ActionDao dao = CompactdbUtil.currentActionDao();
		QueryResult result = dao.executeSQLQuery(sql);
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}

	private void findAll(ActionDao dao) {
		QueryResult findAll = dao.findAll("user");
		System.out.println(findAll.size());
	}

	private void findById(ActionDao dao, DataValue dv) {
		dv.setValue("id", "3509");
		DataValue res = dao.findById("user", dv);
		System.out.println(res);
	}

	private void update(ActionDao dao, DataValue dv) {
		dv.setValue("id", "3509");
		dv.setValue("name", "000");
		dao.update("user", dv);
	}

	private void delete(ActionDao dao, DataValue dv) {
		dv.setValue("id", "3509");

		dao.delete("user", dv);
	}

	private void insert(ActionDao dao, DataValue dv) {
		dv.setValue("name", "233sfd");
		dao.create("user", dv);
	}

}
