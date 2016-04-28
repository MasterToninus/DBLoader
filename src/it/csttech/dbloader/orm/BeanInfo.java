package it.csttech.dbloader.orm;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.text.Annotation;
import it.csttech.dbloader.entities.Sortable;
import java.util.Collections;
import java.util.Comparator;

public class BeanInfo{
  private final Class<?> clazz;
  private final String clazzName;
  private final Method[] allMethods;
  private final List<Method> getters;
  private final List<Method> setters;

  /**
  * @arg è una class implement serializable
  * @return [description]
  */
  public BeanInfo(Class<?> clazz){
    this.clazz = clazz;
    this.clazzName = clazz.getName();
    this.allMethods = clazz.getDeclaredMethods();
    this.getters = fillGetters();
    this.setters = fillSetters();
  }

  private List<Method> fillGetters(){
    List<Method> methodsList = new ArrayList<Method>();
    for (Method m : allMethods){
      if(m.getName().contains("get") | m.getName().contains("is") ){
        methodsList.add(m);
      }
    }
    return sortMethods(methodsList);
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

   public List<Method> getGetters(){
	return getters;
   }

   public String getClassName() {
	return clazzName;
   }
}
