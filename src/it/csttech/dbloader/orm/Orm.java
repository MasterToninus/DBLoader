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
  HashMap<Class<?>,BeanInfo> beanInfoMap ;

  /**
   * [FieldInfo description]
   * @return [description]
   */
  public Orm(){

  }

  //Metodo overload con argomento anche un'oggetto class
  public static BeanInfo buildInfo(String className) throws ClassNotFoundException{
    return new BeanInfo(Class.forName(className));
  }

  public void save(){

  }

  public void getInstance(){

  }

}
