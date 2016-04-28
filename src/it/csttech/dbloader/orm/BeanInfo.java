package it.csttech.dbloader.orm;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.text.Annotation;
import it.csttech.dbloader.entities.Getter;
import java.util.Collections;
import java.util.Comparator;

public class BeanInfo{
  Class<?> clazz;
  Method[] allMethods;

  /**
  * @arg è una class implement serializable
  * @return [description]
  */
  public BeanInfo(Class<?> clazz){
    this.clazz = clazz;
    this.allMethods = clazz.getDeclaredMethods();
  }

  public List<Method> getGetters(){
    List<Method> methodsList = new ArrayList<Method>();
    for (Method m : allMethods){
      if(m.getName().contains("get") | m.getName().contains("is") ){
        methodsList.add(m);
      }
    }
    return sortMethods(methodsList);
  }

  public List<Method> getSetters(){
    List<Method> methodsList = new ArrayList<Method>();
    for (Method m : allMethods){
      if(m.getName().contains("set")){
        methodsList.add(m);
      }
    }
    return methodsList;
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

			Getter getter1 = method1.getAnnotation(Getter.class);
			Getter getter2 = method2.getAnnotation(Getter.class);
			return  getter2.order() - getter1.order();
				
        	}
    	});
	return unSorted;
  }

}
