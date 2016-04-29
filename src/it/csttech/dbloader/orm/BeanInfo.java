package it.csttech.dbloader.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.SortedSet;
import java.util.*;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Comparator;
import it.csttech.dbloader.entities.Getter;
import it.csttech.dbloader.entities.Setter;

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
	fillMethods(allFields);
  }

  private void fillMethods(Method[] allMethods){
    HashMap<String,Method> getters = new HashMap<String,Method>();
    HashMap<String,Method> setters = new HashMap<String,Method>();

    for (Method m : allMethods){
      String name = m.getName();
      System.out.println(name);
      if(name.contains("get")){
        getters.put(name.substring(3).toLowerCase() , m);
      }
      else if(name.contains("is") ){
        getters.put(name.substring(2).toLowerCase()  , m);
      }
      else if(name.contains("set") ){
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
          if((m.getName().contains("get") | m.getName().contains("is")))
            getters.put(f.getName().toLowerCase(), m);
          else if (m.getName().contains("set"))
            setters.put(f.getName().toLowerCase(), m);
        }
      }
    }
  }

  private void fillMethods(Field[] allFields){
    //getters = new HashMap<String,Method>();
    //setters = new HashMap<String,Method>();
    for (Field f : allFields) {
	String name = f.getName();
	if(f.isAnnotationPresent(Setter.class)) {
		StringBuffer methodName = new StringBuffer("set");
		methodName.append(name.substring(0, 1).toUpperCase());
		methodName.append(name.substring(1).toLowerCase());
		//setters.put(name.toLowerCase(), clazz.getMethod(methodName.toString(), f.getType()));
		System.out.println(methodName);
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
