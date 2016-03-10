package com.google.cloud.cassandra.dataflow.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cassandra.utils.Utils;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Table;
import com.google.cloud.cassandra.dataflow.io.CassandraReadIO.Source;
import com.google.cloud.dataflow.sdk.Pipeline;
import com.google.cloud.dataflow.sdk.io.Read;
import com.google.cloud.dataflow.sdk.options.PipelineOptions;
import com.google.cloud.dataflow.sdk.options.PipelineOptionsFactory;
import com.google.cloud.dataflow.sdk.transforms.Flatten;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.google.cloud.dataflow.sdk.values.PCollectionList;

/**
 * Class contains JUnit test case that can be tested in cloud.
 */
public class CassandraReadIOTestWithoutMock {
	private static String[] hosts;
	private static int port;
	private static String keyspace;
	private static Class entityName;
	private static String query;
	private static String tableName;
	private static String rowKey;

	private static Cluster cluster;
	private static Session session;
	private static MappingManager manager;

	static PipelineOptions options;
	static Pipeline p;

	/**
	 * Initial setup for cassandra connection hosts : cassandra server hosts
	 * keyspace : schema name port : port of the cassandra server entityName :
	 * is the POJO class query : simple query conditionalBasedQuery :
	 * conditional based query
	 */
	@BeforeClass
	public static void oneTimeSetUp() {
		hosts = new String[] { "localhost" };
		keyspace = "demo1";
		port = 9042;
		tableName = "emp_info1";
		rowKey = "emp_id";
		entityName = CassandraReadIOTestWithoutMock.EmployeeDetails.class;
		query = QueryBuilder.select().all().from(Utils.KEYSPACE, tableName)
				.toString();

	}

	/**
	 * Creating a pipeline
	 */
	@Before
	public void setUp() {
		options = PipelineOptionsFactory.create();
		p = Pipeline.create(options);
	}

	/**
	 * Test for checking single source split and PCollection object
	 */
	@Test
	public void testToGetSingleSource() {
		try {
			List<Source> splitedSourceList = (List) new CassandraReadIO.Source(
					new CassandraReadConfiguration(hosts, keyspace, 9042,
							tableName, query, rowKey, entityName))
					.splitIntoBundles(1, options);
			Assert.assertEquals(1, splitedSourceList.size());
			Iterator itr = splitedSourceList.iterator();
			CassandraReadIO.Source cs = (Source) itr.next();
			PCollection pCollection = p.apply(Read.from((Source) cs));
			p.run();
			Assert.assertNotNull(pCollection);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test for checking multiple source splits and PCollection Object
	 */
	@Test
	public void testToGetMultipleSplitedSource() {
		try {
			PipelineOptions options = PipelineOptionsFactory.create();
			Pipeline p = Pipeline.create(options);
			List<Source> splitedSourceList = (List) new CassandraReadIO.Source(
					new CassandraReadConfiguration(hosts, keyspace, port,
							tableName, "", rowKey, entityName))
					.splitIntoBundles(4, options);
			Assert.assertEquals(4, splitedSourceList.size());
			Iterator itr = splitedSourceList.iterator();
			List<PCollection> pcoll = new ArrayList<PCollection>();
			while (itr.hasNext()) {
				CassandraReadIO.Source cs = (Source) itr.next();
				pcoll.add(p.apply(Read.from((Source) cs)));
			}
			PCollectionList pCollectionList = PCollectionList.of(pcoll.get(0))
					.and(pcoll.get(1)).and(pcoll.get(2)).and(pcoll.get(3));
			PCollection merged = (PCollection) pCollectionList.apply(Flatten
					.pCollections());
			p.run();
			Assert.assertNotNull(merged);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * static inner class contains employee details
	 */

	@Table(name = "emp_info1", keyspace = "demo1")
	public static class EmployeeDetails implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private int emp_id;
		private String emp_first;
		private String emp_last;
		private String emp_address;
		private String emp_dept;

		public int getEmp_id() {
			return emp_id;
		}

		public void setEmp_id(int emp_id) {
			this.emp_id = emp_id;
		}

		public String getEmp_first() {
			return emp_first;
		}

		public void setEmp_first(String emp_first) {
			this.emp_first = emp_first;
		}

		public String getEmp_last() {
			return emp_last;
		}

		public void setEmp_last(String emp_last) {
			this.emp_last = emp_last;
		}

		public String getEmp_address() {
			return emp_address;
		}

		public void setEmp_address(String emp_address) {
			this.emp_address = emp_address;
		}

		public String getEmp_dept() {
			return emp_dept;
		}

		public void setEmp_dept(String emp_dept) {
			this.emp_dept = emp_dept;
		}

	}

}
