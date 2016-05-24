package it.csttech.dbloader.orm;

import java.util.HashMap;

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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
* Objects relationship mapping
* (Singleton Pattern)
*
* @see Dom Trail : https://docs.oracle.com/javase/tutorial/jaxp/dom/index.html
*/
public class Orm{
  //beaninfo deve essere un attributo di Orm all'interno di una mappa con key il nome
  //anche con metodo getclass
  //possibile costruttore con argomento prop file
  static private Orm orm = null;
  private HashMap<Class<?>,BeanInfo> beanInfoMap = new HashMap<Class<?>,BeanInfo>();
  static final Logger log = LogManager.getLogger();

  //Db connection parameter
  private String username;
  private String password;
  private String driver;
  private String connectionUrl;

  private Orm(){
  }

  private Orm(String xmlConfPath) throws OrmException {
    readOrmConfigFile( xmlConfPath );

    Connection conn = null;
    initDriver(driver);

    //    try{
    log.debug("Requesting Connection to " + connectionUrl );
    //conn = DriverManager.getConnection(connectionUrl,username,password);     //throws SQLException

    //    } catch (java.sql.SQLException ex ) { ex.printStackTrace(); }

  }

  private void readOrmConfigFile(String xmlConfPath) throws OrmException {
    try{
      //Get Document Builder
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      //Build Document
      Document document = dBuilder.parse(new File(xmlConfPath));

      //Normalize the XML Structure; It's just too important !!
      document.getDocumentElement().normalize(); //Scoprire cosa fa

      //Get the two child Nodes (the structure is well-known)
      Element dbSettingElement = (Element)(document.getElementsByTagName("DB-Settings")).item(0);
      Element entitiesElement = (Element)(document.getElementsByTagName("Entities")).item(0);

      //Parsing the db tag:
      this.username = dbSettingElement.getElementsByTagName("User").item(0).getTextContent();
      this.password = dbSettingElement.getElementsByTagName("Password").item(0).getTextContent();
      this.driver = dbSettingElement.getElementsByTagName("Driver").item(0).getTextContent();
      this.connectionUrl = dbSettingElement.getElementsByTagName("ConnectionUrl").item(0).getTextContent();

      //Parsing the entities tag
      NodeList nEntitiesList = entitiesElement.getElementsByTagName("entity");
      for (int temp = 0; temp < nEntitiesList.getLength(); temp++)
      {
        Node node = nEntitiesList.item(temp);
        System.out.println("");    //Just a separator
        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
          addBeanClass( ((Element) node).getAttribute("class"));
        }
      }

    } catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException | ClassNotFoundException ex ) {
      throw new OrmException("Config file parse failed", ex);
    }

  }

  private void initDriver(final String driver) {
    try {
      log.trace("Driver Loading : " + driver);
      Class.forName(driver);
    } catch (final Exception e) {
      log.error("Database driver error:" + e.getMessage());
    }
  }


  private void addBeanClass(String beanClassName) throws ClassNotFoundException {
    addBeanClass(Class.forName(beanClassName));
  }

  private void addBeanClass(Class<?> beanClass){
    BeanInfo beanInfo = new BeanInfo(beanClass);
    beanInfoMap.put(beanClass, beanInfo);
  }


  /**
  * [getInstance description]
  * 	Fare overload che legge file di properties
  * @param  beanClassName [description]
  * @return               [description]
  */
  static public Orm getInstance(String xmlConfPath) throws OrmException {
    if(orm == null) orm = new Orm(xmlConfPath);
    return orm;
  }

  //Istance methods

  public BeanInfo getBeanInfo(String beanClassName) throws OrmException  {
    BeanInfo buffer = null;
    try{
      buffer = getBeanInfo(Class.forName(beanClassName));
    } catch ( ClassNotFoundException ex ){
      throw new OrmException("Bean class not loaded",ex);
    }
    return buffer;
  }

  public BeanInfo getBeanInfo(Class<?> beanClass) {
    return beanInfoMap.get(beanClass);
  }

  public void save(Object bean){
	  Class<?> beanClazz = bean.getClass();
	  HashMap<String,Method> getters = beanInfoMap.get(beanClazz).getGetters();
	  
	  try {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(beanClazz);
			enhancer.setCallback(new BeanInvocationHandler(bean));

			Object proxy = enhancer.create();
			
			for (String key : getters.keySet()) {
			      Method m = getters.get(key);
			      m.invoke(proxy);
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }


}
