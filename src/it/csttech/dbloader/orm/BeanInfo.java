package it.csttech.dbloader.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.SortedSet;
import java.util.*;
import java.text.Annotation;
import it.csttech.dbloader.entities.Sortable;
import java.util.Collections;
import java.util.Comparator;

public class BeanInfo{
  private final Class<?> clazz;
  private final String clazzName;
  private final HashMap<String,Method> getters;
  private final HashMap<String,Method> setters;

  /**
  * @arg è una class implement serializable
  * @return [description]
  */
  public BeanInfo(Class<?> clazz){
    this.clazz = clazz;
    this.clazzName = clazz.getName();
    Method[] allMethods = clazz.getDeclaredMethods();
    Field[] allFields = clazz.getFields();
    fillMethods(allFields, allMethods);
  }

  private void fillMethods(Field[] allFields; Method[] allMethods){
    for (Method m : allMethods){
	for (Field f : allFields) {
	    if(m.getName.toLowerCase().contains(f.getName.toLowerCase())) {
      		if((m.getName().contains("get") | m.getName().contains("is")))
			getters.put(f.getName().toLowerCase(), m)
      		else if (m.getName().contains("set"))
			setters.put(f.getName().toLowerCase(), m)
	    }
	}
    }

    SortedSet<Method> methodsSet = new TreeSet<Method>( //Comparator non sarà qui. è classe o istanza?
      new Comparator<Method>() {
          	@Override
          	public int compare(Method method2, Method method1) {
              Sortable sort1 = method1.getAnnotation(Sortable.class);
  			      Sortable sort2 = method2.getAnnotation(Sortable.class);
  			    return  sort2.index() - sort1.index();
          	}
      	});
    for (Method m : allMethods){
      if(m.getName().contains("get") | m.getName().contains("is") ){
        methodsSet.add(m);
      }
    }
    return methodsSet;
  }

  public List<Method> fillSetters(){
    List<Method> methodsList = new ArrayList<Method>();
    for (Method m : allMethods){
      if(m.getName().contains("set")){
        methodsList.add(m);
      }
    }
    return sortMethods(methodsList);
  }

  public void test(){
    for (Method m : allMethods) {
      System.out.format("invoking %s( )%n", m.getName());
      m.setAccessible(true);
    }
  }

  private List<Method> sortMethods( List<Method> unSorted ) {
	Collections.sort(unSorted, new Comparator<Method>() {
        	@Override
        	public int compare(Method method2, Method method1) {

			Sortable sort1 = method1.getAnnotation(Sortable.class);
			Sortable sort2 = method2.getAnnotation(Sortable.class);
			return  sort2.index() - sort1.index();

        	}
    	});
	return unSorted;
  }

  public List<Method> getSetters(){
	  return setters;
  }

  public SortedSet<Method> getGetters(){
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
