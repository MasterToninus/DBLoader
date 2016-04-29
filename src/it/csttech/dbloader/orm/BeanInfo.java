package it.csttech.dbloader.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.SortedSet;
import java.util.*;
import java.text.Annotation;
import java.util.Collections;
import java.util.Comparator;

public class BeanInfo{
  private final Class<?> clazz;
  private final String clazzName;
  private  HashMap<String,Method> getters;
  private  HashMap<String,Method> setters;

  /**
  * @arg è una class implement serializable
  * @return [description]
  */
  public BeanInfo(Class<?> clazz){
    this.clazz = clazz;
    this.clazzName = clazz.getName();
    Method[] allMethods = clazz.getDeclaredMethods();
    Field[] allFields = clazz.getDeclaredFields();
    fillMethods(allFields, allMethods);
  }

  private void fillMethods(Method[] allMethods){
    HashMap<String,Method> gettersBuffer = new HashMap<String,Method>();
    HashMap<String,Method> settersBuffer = new HashMap<String,Method>();

    for (Method m : allMethods){
      String name = m.getName();
      System.out.println(name);
      if(name.contains("get")){
        gettersBuffer.put(name.substring(3).toLowerCase() , m);
      }
      else if(name.contains("is") ){
        gettersBuffer.put(name.substring(2).toLowerCase()  , m);
      }
      else if(name.contains("set") ){
        settersBuffer.put(name.substring(3).toLowerCase()  , m);
      }
    }
    getters = new HashMap<String,Method>(gettersBuffer);
    setters = new HashMap<String,Method>(settersBuffer);
  }

  private void fillMethods(Field[] allFields, Method[] allMethods){
    getters = new HashMap<String,Method>();
    setters = new HashMap<String,Method>();
    for (Method m : allMethods){
      for (Field f : allFields) {
        if(m.getName().toLowerCase().contains(f.getName().toLowerCase())) {
          if((m.getName().contains("get") | m.getName().contains("is")))
            getters.put(f.getName().toLowerCase(), m);
          else if (m.getName().contains("set"))
            setters.put(f.getName().toLowerCase(), m);
        }
      }
    }
  }



  public void test(){
    for (String key : this.getters.keySet()) {
      System.out.format("Key = %s \t %s \t %s %n",key ,getters.get(key).getName(), setters.get(key).getName() );
    }
  }

  public HashMap<String,Method> getSetters(){
	  return setters;
  }

  public HashMap<String,Method> getGetters(){
	  return getters;
  }

  public String getClassName() {
	 return clazzName;
  }

  //Restituisco object. dopo andrà castato!
  public Object getInstance() throws java.lang.InstantiationException, java.lang.IllegalAccessException {
    return clazz.newInstance();
  }
}
