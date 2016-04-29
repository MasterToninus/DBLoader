package it.csttech.dbloader.test;

import it.csttech.dbloader.entities.*;
import it.csttech.dbloader.loader.*;
import it.csttech.dbloader.orm.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import java.lang.reflect.InvocationTargetException;

import java.util.Date;
import java.util.Random;
import java.util.List;

/**
* A cli interface to launch a simple ETL Suite
*
* Questo va messo in una classe di test
*
* <p>
* main che legge le prop con l'opt -D
* @see http://stackoverflow.com/questions/5045608/proper-usage-of-java-d-command-line-parameters
* </p>
*
* @author drago-orsone, MasterToninus
*
*/
public class ReflectionDbLoader {

  static final Logger log = LogManager.getLogger();

  public static void main(String[] args ){


    Properties prop = readProperties(System.getProperty("prop.File"));

    try{
      BeanInfo beanInfo = Orm.buildInfo(prop.getProperty("bean.class"));
      beanInfo.test();

      Object record =  beanInfo.getInstance(); //si puo fare di meglio? Interfaccia record?
      Object[] object = {1, "Pippo", new Date(1450656000000L), 1.56, true};
      System.out.println("\n");
      /*for (int i = 0; i < 5; i++) {
        beanInfo.getSetters().get(i).invoke(record, object[i]);
        System.out.println(beanInfo.getGetters().get(i).invoke(record));
      }*/
      int j = 0;
      java.util.HashMap<String,Method> getters = beanInfo.getGetters();
      /*
      for(Method m : getters) { //SortedSet o List sorted manually?
        beanInfo.getSetters().get(j).invoke(record, object[j]);
        j++;
        System.out.println(m.invoke(record));
      }
      */
    } catch ( Exception ex){
      ex.printStackTrace();
    }



  }

  public static Properties readProperties( String propFile ){
    Properties prop = new Properties();
    InputStream input = null;

    try {
      input = new FileInputStream(propFile);
      // load a properties file
      prop.load(input);
    } catch (IOException ex) {
      log.error(ex.getMessage());
      log.debug(ex);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          log.error(e.getMessage());
          log.debug(e);
        }
      }
    }
    return prop;
  }

  /**
  * Idea:
  * 	voglio un metodo che prenda un oggetto di tipo class o beaninfo
  * 	testi che tale classe sia un Bean
  * 	generi un istanza di questo bean
  * 	la setti
  * 	restituisca il bean pieno
  *
  * @author drago-orsone, MasterToninus
  *
  */
  //public<T> T randomBean(BeanInfo beanInfo){
  //  T bean = beanInfo.getInstance();
  /*
  public static Object randomBean(BeanInfo beanInfo) throws IllegalAccessException, InstantiationException, InvocationTargetException{
    Object bean = beanInfo.getInstance();
    java.util.HashMap<String,Method> beanSetters = beanInfo.getSetters();
    Random random = new Random(1L); //Long seed, idealmente la data in millisecondi
    for (Method m : beanSetters){
      System.out.println(m.getGenericParameterTypes());
      m.invoke(bean, random.nextLong());
    }
    return bean;
  }
*/

}
