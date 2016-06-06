package it.csttech.dbloader.orm;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
 *	Implenting AutoCloseable for  the try-with-resources statement.
 *
 * @see <a href="https://docs.oracle.com/javase/tutorial/jaxp/dom/index.html">
 *      Dom Trail</a>
 */
public class Orm implements AutoCloseable{

	static private Orm orm = null;
	static final Logger log = LogManager.getLogger(Orm.class.getName());

	private DataSource conn_pooled = null;
	private HashMap<Class<?>, BeanInfo> beanInfoMap = new HashMap<Class<?>, BeanInfo>();
	
	private Orm() { }

	/**
	 * 
	 * @param xmlConfPath
	 * @throws OrmException
	 */
	@SuppressWarnings("unchecked")
	private Orm(String xmlConfPath) throws OrmException {
		log.info("Instantiating ORM object.");
		try {

			Map<String,Object> properties = readOrmConfigFile(xmlConfPath);

			setSystemProperties((Map<String,String>) properties.get("system"));
			createConnections((String) properties.get("url"));
			initBeanClasses((Set<String>)  properties.get("classes"));

		} catch (java.sql.SQLException ex) {
			throw new OrmException(ex.getMessage(), ex);
		}
		
	}

	/**
	 * 	
	 * @param xmlConfPath
	 * @return HashMap: keys = ("classes", "system", "url")
	 * @throws OrmException
	 */
	private Map<String,Object> readOrmConfigFile(String xmlConfPath) throws OrmException {
		log.info("Loading ORM configuration from : " + xmlConfPath);
		Map<String,Object> confMap = new HashMap<String,Object>();
		try {
			File xmlFile = new File(xmlConfPath);
			if (!xmlFile.canRead()) throw new OrmException("Cannot Read " + xmlConfPath);
			// Get Document Builder
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			// Build Document
			Document document = dBuilder.parse(xmlFile);

			// Normalize the XML Structure; It's just too important !!
			document.getDocumentElement().normalize(); // Scoprire cosa fa

			// Get the two child Nodes (the structure is well-known)
			Element dbSettingElement = (Element) (document.getElementsByTagName("DB-Settings")).item(0);
			Element entitiesElement = (Element) (document.getElementsByTagName("Entities")).item(0);
			Element c3p0Element = (Element) (document.getElementsByTagName("c3p0-Settings")).item(0);

			// Parsing the db tag:
			String username = dbSettingElement.getElementsByTagName("User").item(0).getTextContent();
			String password = dbSettingElement.getElementsByTagName("Password").item(0).getTextContent();
			String driver = dbSettingElement.getElementsByTagName("Driver").item(0).getTextContent();
			String connectionUrl = dbSettingElement.getElementsByTagName("ConnectionUrl").item(0).getTextContent();

			// Parsing the entities tag
			NodeList nEntitiesList = entitiesElement.getElementsByTagName("entity");
			Set<String> classSet = new java.util.HashSet<String>();
			for (int temp = 0; temp < nEntitiesList.getLength(); temp++) {
				Node node = nEntitiesList.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					classSet.add(((Element) node).getAttribute("class"));
				}
			}
			confMap.put("classes", classSet);

			// Parsing the c3p0-Properties tag			
			NodeList nPropertyList = c3p0Element.getElementsByTagName("property");
			Map<String,String> systemProp = new HashMap<String,String>(); 
			for (int temp = 0; temp < nPropertyList.getLength(); temp++) {
				Node node = nPropertyList.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					systemProp.put(((Element) node).getAttribute("name"), ((Element) node).getTextContent());
				}
			}
			confMap.put("system", systemProp);
			
			log.debug("Driver Loading : " + driver);
			Class.forName(driver);

			String url = connectionUrl + "?user=" + username + "&password=" + password;
			confMap.put("url", url);
			
			return confMap;

		} catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException
				| ClassNotFoundException ex) {
			throw new OrmException("Config file parse failed", ex);
		}

	}
	
	private void setSystemProperties(Map<String,String> systemProp){
		log.debug("Loading system properties : " + systemProp);
		for(String key : systemProp.keySet()){
			System.setProperty(key, systemProp.get(key));
		}	
	}
	
	private void createConnections(String url) throws java.sql.SQLException {
		log.debug("Requesting Connection to " + url );

		DataSource conn_unpooled = DataSources.unpooledDataSource(url);
		conn_pooled = DataSources.pooledDataSource( conn_unpooled );
		if (conn_pooled != null)
			log.debug("connection pool established!!!");	
	}
	
	private void initBeanClasses(Set<String> classes) throws OrmException{
		for(String name : classes){
			addBeanClass(name);
		}
	}
	
	public void addBeanClass(String beanClassName) throws OrmException {
		try {
			addBeanClass(Class.forName(beanClassName));
		} catch (ClassNotFoundException e) {
			throw new OrmException("Class " + beanClassName + " not Found", e);
		}
	}

	public void addBeanClass(Class<?> beanClass) throws OrmException {
		BeanInfo beanInfo;
		try (Connection conn = conn_pooled.getConnection()) {
			beanInfo = new BeanInfo(beanClass);
			beanInfoMap.put(beanClass, beanInfo);
			
			// execute Create Table sl statment SQL stetement 
			log.debug("Executing \"Create Table\" statement.");
			PreparedStatement preparedStatementCreate = conn.prepareStatement(beanInfo.getCreateTableQuery());
			preparedStatementCreate.executeUpdate();
			preparedStatementCreate.close();
			
		} catch (BeanInfoException | SQLException  e) {
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
		if(!beanInfoMap.containsKey(beanClazz)) throw new OrmException(beanClazz + " not loaded.");

		//Creating a Proxy of the JavaBean
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(beanClazz);
		enhancer.setCallback(new BeanInvocationHandler(bean));
		Object proxy = enhancer.create();		

		//Storing local variables
		BeanInfo beanInfo = beanInfoMap.get(beanClazz);
		log.info("Loading " + bean + " on " + beanInfo.getTableName());		
		Set<String> fieldKeySet = beanInfo.getFieldKeySet();
		Map<String, Method> getters = beanInfo.getGetters();

		log.debug("Getting pooled connection.");
		try (Connection conn = conn_pooled.getConnection()) {

			// execute insert SQL stetement
			log.debug("Executing \"Insert record\" statement.");
			PreparedStatement preparedStatementInsert = conn.prepareStatement(beanInfo.getInsertQuery());
			int i = 1;
			for (String key : fieldKeySet) {
				Method m = getters.get(key);
				if (!beanInfo.getFieldInfoMap().get(key).isAutoIncrement()) {
					preparedStatementInsert.setObject(i, m.invoke(proxy));
					i++;
				}
			}
			preparedStatementInsert.executeUpdate();

			log.debug("closing statement.");
			preparedStatementInsert.close();


			log.trace(beanInfo.getClassName() + " record is inserted into " + beanInfo.getTableName() + " table!");

		} catch (Exception e) {
			throw new OrmException("Saving to DB failed.", e);
		}
	}

	public void close() throws OrmException{
		log.info("Releasing Connections.");
		if (conn_pooled != null) {
			try {
				DataSources.destroy(conn_pooled);
				log.trace(" Connection freed.");
			} catch (SQLException sqlex) {
				throw new OrmException(sqlex);
				//log.fatal(sqlex.getMessage());
			}
		}
	}
}
