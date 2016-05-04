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
      Orm orm = Orm.getInstance();
      orm.addBeanClass(prop.getProperty("bean.class"));
      BeanInfo beanInfo = orm.getBeanInfo(prop.getProperty("bean.class"));
      beanInfo.test();

      Object record =  beanInfo.getInstance(); //si puo fare di meglio? Interfaccia record?
      java.util.HashMap<String,Method> getters = beanInfo.getGetters();


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




}
