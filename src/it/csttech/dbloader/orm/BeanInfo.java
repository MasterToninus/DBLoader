package it.csttech.dbloader.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import java.util.*;
import java.lang.annotation.Annotation;

import it.csttech.dbloader.entities.Getter;
import it.csttech.dbloader.entities.Setter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BeanInfo{

  static final Logger log = LogManager.getLogger(BeanInfo.class.getName());

  private final Class<?> clazz;
  private final String clazzName;
  private  HashMap<String, Method> getters;
  private  HashMap<String, Method> setters;

  /**
   * [BeanInfo description]
   * @param  clazz [description]
   * @return       [description]
   */
  public BeanInfo(Class<?> clazz) {
    this.clazz = clazz;
    this.clazzName = clazz.getName();
    Method[] allMethods = clazz.getDeclaredMethods();
    Field[] allFields = clazz.getDeclaredFields();
    //fillMethods(allFields, allMethods);
	  try{
      fillMethods(allFields);
    } catch(Exception e ){
      //Questa eccezione di metodo non trovato secondo me va tradotta in una beaninfo exception (annotation dice che il field è un setter/getter ma il corrispettivo metodo non c'è)
      //Credo che tutte le exception di reflection andrebbero wrappate in un'eccezzione di beaninfo.
      e.printStackTrace();
    }
  }

  /**
   * Non è failsafe perchè cerca metodi che contengono get /set nel nome
   * @param allMethods [description]
   */
  private void fillMethods(Method[] allMethods){
    getters = new HashMap<String,Method>();
    setters = new HashMap<String,Method>();
    for (Method m : allMethods){
      String name = m.getName();
      if(name.startsWith("get")){
        getters.put(name.substring(3).toLowerCase() , m);
      }
      else if(name.startsWith("is") ){
        getters.put(name.substring(2).toLowerCase()  , m);
      }
      else if(name.startsWith("set") ){
        setters.put(name.substring(3).toLowerCase()  , m);
      }
    }
  }

  private void fillMethods(Field[] allFields, Method[] allMethods){
    getters = new HashMap<String,Method>();
    setters = new HashMap<String,Method>();
    for (Method m : allMethods){
      for (Field f : allFields) {
        if(m.getName().toLowerCase().contains(f.getName().toLowerCase())) {
          if((m.getName().startsWith("get") | m.getName().startsWith("is")))
            getters.put(f.getName().toLowerCase(), m);
          else if (m.getName().startsWith("set"))
            setters.put(f.getName().toLowerCase(), m);
        }
      }
    }
  }

  private void fillMethods(Field[] allFields) throws NoSuchMethodException {
    getters = new HashMap<String,Method>();
    setters = new HashMap<String,Method>();
    for (Field f : allFields) {
      String name = f.getName();
      if(f.isAnnotationPresent(Setter.class)) {
        StringBuilder methodName = new StringBuilder("set"); // è consigliabile usare stringbuilder se non si hanno thread concorrenti
        methodName.append(name.substring(0, 1).toUpperCase());
        methodName.append(name.substring(1).toLowerCase());
        setters.put(name.toLowerCase(), clazz.getMethod(methodName.toString(), f.getType()));
      }
      if(f.isAnnotationPresent(Getter.class)){
        StringBuilder methodName = new StringBuilder();
        if(f.getType().isAssignableFrom(Boolean.TYPE)) methodName.append("is");
        else methodName.append("get");
        methodName.append(name.substring(0, 1).toUpperCase());
        methodName.append(name.substring(1).toLowerCase());
        getters.put(name.toLowerCase(), clazz.getMethod(methodName.toString(), (Class<?>[]) null));
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

class BeanException extends Exception {
  private static final long serialVersionUID = 1L;
}
