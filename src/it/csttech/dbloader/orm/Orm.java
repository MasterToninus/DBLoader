package it.csttech.dbloader.orm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import net.sf.cglib.proxy.Enhancer;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;

import com.mchange.v2.c3p0.DataSources;


import javax.sql.DataSource;

/**
 * Objects relationship mapping (Singleton Pattern) beaninfo deve essere un
 * attributo di Orm all'interno di una mappa con key il nome anche con metodo
 * getclass possibile costruttore con argomento prop file
 *
 * @see <a href="https://docs.oracle.com/javase/tutorial/jaxp/dom/index.html">
 *      Dom Trail</a>
 */
public class Orm {

	static private Orm orm = null;
	private HashMap<Class<?>, BeanInfo> beanInfoMap = new HashMap<Class<?>, BeanInfo>();
	static final Logger log = LogManager.getLogger(Orm.class.getName());

	// private Connection conn = null;
	private DataSource conn_pooled = null;

	private Orm() { }

	private Orm(String xmlConfPath) throws OrmException {
		log.info("Loading ORM configuration from : " + xmlConfPath);
		String url = readOrmConfigFile(xmlConfPath);
		Map<String, Object> overrides = c3p0Opts();

		try {
			// log.debug("Requesting Connection to " + connectionUrl );
			// conn = DriverManager.getConnection(url); // throws SQLException
			DataSource conn_unpooled = DataSources.unpooledDataSource(url);
			conn_pooled = DataSources.pooledDataSource(conn_unpooled, overrides);
			if (conn_pooled != null)
				log.debug("connection pool established!!!");

		} catch (java.sql.SQLException ex) {
			throw new OrmException(ex.getMessage(), ex);
		}
	}

	private String readOrmConfigFile(String xmlConfPath) throws OrmException {
		try {
			// Get Document Builder
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			// Build Document
			Document document = dBuilder.parse(new File(xmlConfPath));

			// Normalize the XML Structure; It's just too important !!
			document.getDocumentElement().normalize(); // Scoprire cosa fa

			// Get the two child Nodes (the structure is well-known)
			Element dbSettingElement = (Element) (document.getElementsByTagName("DB-Settings")).item(0);
			Element entitiesElement = (Element) (document.getElementsByTagName("Entities")).item(0);

			// Parsing the db tag:
			String username = dbSettingElement.getElementsByTagName("User").item(0).getTextContent();
			String password = dbSettingElement.getElementsByTagName("Password").item(0).getTextContent();
			String driver = dbSettingElement.getElementsByTagName("Driver").item(0).getTextContent();
			String connectionUrl = dbSettingElement.getElementsByTagName("ConnectionUrl").item(0).getTextContent();

			// Parsing the entities tag
			NodeList nEntitiesList = entitiesElement.getElementsByTagName("entity");
			for (int temp = 0; temp < nEntitiesList.getLength(); temp++) {
				Node node = nEntitiesList.item(temp);
				System.out.println(""); // Just a separator
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					addBeanClass(((Element) node).getAttribute("class"));
				}
			}

			log.debug("Driver Loading : " + driver);
			Class.forName(driver);

			String url = connectionUrl + "?user=" + username + "&password=" + password;
			return url;

		} catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException
				| ClassNotFoundException ex) {
			throw new OrmException("Config file parse failed", ex);
		}

	}

	/**
	 * <a href="www.mchange.com/projects/c3p0/#otherProperties">link</a>
	 * @return
	 */
	private Map<String, Object> c3p0Opts() {
		Map<String, Object> overrides = new HashMap<String, Object>();
		overrides.put("maxStatements", "200"); // Stringified property values
		overrides.put("maxPoolSize", new Integer(50)); // "boxed primitives"
		//c3p0 does not work with log4j2 yet. bummer.
		System.setProperty("com.mchange.v2.log.MLog", "fallback");
		//Suppressing all default c3p0 standard out logs.
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");
		return overrides;
	}

	public void addBeanClass(String beanClassName) throws OrmException {
		try {
			addBeanClass(Class.forName(beanClassName));
		} catch (ClassNotFoundException e) {
			throw new OrmException("Class not Found", e);
		}
	}

	public void addBeanClass(Class<?> beanClass) throws OrmException {
		BeanInfo beanInfo;
		try {
			beanInfo = new BeanInfo(beanClass);
			beanInfoMap.put(beanClass, beanInfo);
		} catch (BeanInfoException e) {
			throw new OrmException("", e);
		}

	}

	/**
	 * 
	 * @param xmlConfPath
	 * @return
	 * @throws OrmException
	 */
	static public Orm getInstance(String xmlConfPath) throws OrmException {
		if (orm == null)
			orm = new Orm(xmlConfPath);
		return orm;
	}

	public BeanInfo getBeanInfo(String beanClassName) throws OrmException {
		BeanInfo buffer = null;
		try {
			buffer = getBeanInfo(Class.forName(beanClassName));
		} catch (ClassNotFoundException ex) {
			throw new OrmException("Bean class not loaded", ex);
		}
		return buffer;
	}

	public BeanInfo getBeanInfo(Class<?> beanClass) {
		return beanInfoMap.get(beanClass);
	}

	public void save(Object bean) throws OrmException {
		Class<?> beanClazz = bean.getClass();
		BeanInfo beanInfo = beanInfoMap.get(beanClazz);
		Set<String> fieldKeySet = beanInfo.getFieldKeySet();
		Map<String, Method> getters = beanInfo.getGetters();

		try {
			//Creating a Proxy of the JavaBean
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(beanClazz);
			enhancer.setCallback(new BeanInvocationHandler(bean));
			Object proxy = enhancer.create();

			Connection conn = conn_pooled.getConnection();
			PreparedStatement preparedStatementCreate = conn.prepareStatement(beanInfo.getCreateTableQuery());

			preparedStatementCreate.executeUpdate();

			PreparedStatement preparedStatementInsert = conn.prepareStatement(beanInfo.getInsertQuery());

			int i = 1;
			for (String key : fieldKeySet) {
				Method m = getters.get(key);

				if (!beanInfo.getFieldInfoMap().get(key).isAutoIncrement()) {
					preparedStatementInsert.setObject(i, m.invoke(proxy));
					i++;
				}
			}

			// execute insert SQL stetement
			preparedStatementInsert.executeUpdate();

			preparedStatementCreate.close();
			preparedStatementInsert.close();
			conn.close();
			// TODO chiedere: è giusto? rilascia o distrugge? nel conn pool
			// rimane?

			log.trace(beanInfo.getClassName() + " record is inserted into " + beanInfo.getTableName() + " table!");

		} catch (Exception e) {
			throw new OrmException("Saving to DB failed.", e);
		}
	}

	public void destroy() {
		log.info("Releasing Connections");
		if (conn_pooled != null) {
			try {
				DataSources.destroy(conn_pooled);
				log.trace(" Connection freed");
			} catch (SQLException sqlex) {
				log.fatal(sqlex.getMessage());
			}
		}
	}
}
