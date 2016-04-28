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

import java.util.Date;

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
	Object[] object = {1, "Pippo", new Date(1450656000000L), 1.56, true};
	System.out.println("\n");
	for (int i = 0; i < 5; i++) {
		beanInfo.getSetters().get(i).invoke(record, object[i]);
		System.out.println(beanInfo.getGetters().get(i).invoke(record));
	}
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
