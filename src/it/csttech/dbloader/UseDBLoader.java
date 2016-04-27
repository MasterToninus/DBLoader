package it.csttech.dbloader;

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

/**
* A cli interface to launch a simple ETL Suite
*
* <p>
* main che legge le prop con l'opt -D
* @see http://stackoverflow.com/questions/5045608/proper-usage-of-java-d-command-line-parameters
* </p>
*
* @author drago-orsone, MasterToninus
*
*/
public class UseDBLoader {

  static final Logger log = LogManager.getLogger();

  public static void main(String[] args ){


    Properties prop = readProperties(System.getProperty("prop.File"));

    try{
      BeanInfo beanInfo = Orm.buildInfo(prop.getProperty("bean.class"));
      beanInfo.test();

      Record record = new Record();
      Method metodo = beanInfo.getSetters().get(0);
      System.out.println(metodo.getName() + " " + metodo.getReturnType() + " " + metodo.getParameterTypes());
      metodo.invoke(record,"1");
      System.out.format(" %s ", record.getName());




    } catch ( ClassNotFoundException | IllegalAccessException | InvocationTargetException ex){
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
