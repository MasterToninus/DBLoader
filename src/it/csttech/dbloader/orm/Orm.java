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
 * Objects relationship mapping (Singleton Pattern)
 *
 * @see Dom Trail : https://docs.oracle.com/javase/tutorial/jaxp/dom/index.html
 */
public class Orm {
	// beaninfo deve essere un attributo di Orm all'interno di una mappa con key
	// il nome
	// anche con metodo getclass
	// possibile costruttore con argomento prop file
	static private Orm orm = null;
	private HashMap<Class<?>, BeanInfo> beanInfoMap = new HashMap<Class<?>, BeanInfo>();
	static final Logger log = LogManager.getLogger();
	
	//private Connection conn = null;
	private DataSource conn_pooled = null;

	private Orm() {
	}

	private Orm(String xmlConfPath) throws OrmException {

		String url = readOrmConfigFile(xmlConfPath);
		Map<String, Object> overrides = c3p0Opts();

		try {
			// log.debug("Requesting Connection to " + connectionUrl );
			//conn = DriverManager.getConnection(url); // throws SQLException
			DataSource conn_unpooled = DataSources.unpooledDataSource(url);
			conn_pooled = DataSources.pooledDataSource( conn_unpooled, overrides );
			if (conn_pooled != null)
				log.debug("connection pool established!!!");

		} catch (java.sql.SQLException ex) {
			throw new OrmException(ex.getMessage());
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
	
	Map<String, Object> c3p0Opts() {
		Map<String, Object> overrides = new HashMap<String, Object>();
		overrides.put("maxStatements", "200");         //Stringified property values work
		overrides.put("maxPoolSize", new Integer(50)); //"boxed primitives" also work
		overrides.put("com.mchange.v2.log.MLog", "log4j");
		return overrides;
	}

	private void addBeanClass(String beanClassName) throws ClassNotFoundException {
		addBeanClass(Class.forName(beanClassName));
	}

	private void addBeanClass(Class<?> beanClass) {
		BeanInfo beanInfo = new BeanInfo(beanClass);
		beanInfoMap.put(beanClass, beanInfo);
	}

	/**
	 * [getInstance description] Fare overload che legge file di properties
	 * 
	 * @param beanClassName
	 *            [description]
	 * @return [description]
	 */
	static public Orm getInstance(String xmlConfPath) throws OrmException {
		if (orm == null)
			orm = new Orm(xmlConfPath);
		return orm;
	}

	// Istance methods

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

	public void save(Object bean) {
		Class<?> beanClazz = bean.getClass();
		BeanInfo beanInfo = beanInfoMap.get(beanClazz);
		Set<String> fieldKeySet = beanInfo.getFieldKeySet();
		Map<String, Method> getters = beanInfo.getGetters();

		try {
			
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
			conn.close(); //TODO chiedere: è giusto? rilascia o distrugge? nel conn pool rimane?
			
			log.trace("Record is inserted into" + " ? " + " table!");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void destroy() {
		if (conn_pooled != null) {
			try {
				DataSources.destroy(conn_pooled);
			} catch (SQLException sqlex) {
				log.fatal(sqlex.getMessage());
			}
		}
	}
}
