package it.csttech.dbloader.orm;

import java.util.HashMap;

/**
 * Objects relationship mapping
 * (Singleton Pattern)
 */
public class Orm{
  //beaninfo deve essere un attributo di Orm all'interno di una mappa con key il nome
  //anche con metodo getclass
  //possibile costruttore con argomento prop file
  private HashMap<Class<?>,BeanInfo> beanInfoMap = new HashMap<Class<?>,BeanInfo>();
  static private Orm orm = null;

  private Orm(){
  }

  /**
   * [FieldInfo description]
   * @return [description]
   */

  static public void save(Class<?> beanClass){ //gli passo un  bean e lo carica poi con getclass trovo l'oggetto relativo nel beaninfo

  }

  /**
   * [getInstance description]
   * 	Fare overload che legge file di properties
   * @param  beanClassName [description]
   * @return               [description]
   */
  static public Orm getInstance() {
    if(orm == null) orm = new Orm();
    return orm;
  }

  public void addBeanClass(String beanClassName) throws ClassNotFoundException {
    addBeanClass(Class.forName(beanClassName));
  }

  public void addBeanClass(Class<?> beanClass){
    BeanInfo beanInfo = new BeanInfo(beanClass);
    beanInfoMap.put(beanClass, beanInfo);
  }

  public BeanInfo getBeanInfo(String beanClassName) throws ClassNotFoundException {
    return getBeanInfo(Class.forName(beanClassName));
  }

  public BeanInfo getBeanInfo(Class<?> beanClass) {
    return beanInfoMap.get(beanClass);
  }

}
