package it.csttech.dbloader.orm;

/**
 * Objects relationship mapping
 */
public class Orm{

  /**
   * [FieldInfo description]
   * @return [description]
   */
  public Orm(){

  }
  public static BeanInfo buildInfo(String className) throws ClassNotFoundException{
    return new BeanInfo(Class.forName(className));
  }

}
