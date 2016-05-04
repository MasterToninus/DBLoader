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
  private HashMap<String,BeanInfo> beanInfoMap = new HashMap<String,BeanInfo>();
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
    BeanInfo beanInfo = new BeanInfo(Class.forName(beanClassName));
    beanInfoMap.put(beanClassName, beanInfo);
  }

  public BeanInfo getBeanInfo(String beanClassName) throws ClassNotFoundException {
    return beanInfoMap.get(beanClassName);
  }


}
