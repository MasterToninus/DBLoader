package it.csttech.dbloader;

import it.csttech.dbloader.entities.BeanBuilder;
import it.csttech.dbloader.orm.*;
import it.csttech.dbloader.test.MockRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Run with -Dprop.File=file.prop
 * 
 * @author drago-orsone, MasterToninus
 *
 */
public class UseDBLoader {

	static final Logger log = LogManager.getLogger(UseDBLoader.class.getName());

	public static void main(String[] args) {
		//TODO temporary workaround
		//Exception when it is not passed any propFile to the program?
		if(System.getProperty("prop.File") == null){
			System.setProperty("prop.File","dbloader_default.properties");
			log.error("dbloader.properties not found. Loading Defaults");
		}
		
		Properties prop = readProperties(System.getProperty("prop.File"));
		
		new UseDBLoader(prop.getProperty("orm.config").trim());
	}

	public static Properties readProperties(String propFile) {
		log.info("Parsing properties File : " + propFile);
		Properties prop = new Properties();

		//Java 7 AutoClosable
		try( InputStream input = new FileInputStream(propFile) ) {
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			log.error(ex.getMessage());
			log.debug(ex);
		}
		return prop;
	}
	
	/**
	 * TODO temporary constructor testing the orm save capability
	 * @param ormConfigFile
	 */
	public UseDBLoader(String ormConfigFile){
		BeanInfo beanInfo = null;
		MockRecord mockRecord = null;

		try ( Orm orm = Orm.getInstance(ormConfigFile) ) {

			//TODO (temp) usage scenario of ORM
			log.info("Loading entity to db " );
			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.Record");
			mockRecord = new MockRecord(beanInfo);
			orm.save(mockRecord.next());

			log.info("Loading entity to db " );
			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.CstEmployee");
			mockRecord = new MockRecord(beanInfo);
			orm.save(mockRecord.next());

			log.info("Loading entity to db " );
			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.Address");
			mockRecord = new MockRecord(beanInfo);
			orm.save(mockRecord.next());

			//TODO (temp) usage scenario of BeanBuilder
			log.info("Loading entity to db " );
			BeanBuilder bb = new BeanBuilder();
			bb.init("Picasso");
			bb.addField("id", Integer.class, true, true, true, true, true);
			bb.addField("name", String.class, false, true, true, false, false);
			Class<?> clazz = bb.load();
			orm.addBeanClass(clazz);
			beanInfo = orm.getBeanInfo(clazz);
			mockRecord = new MockRecord(beanInfo);
			orm.save(mockRecord.next());

		} catch (OrmException ex) {
			log.error(ex.getMessage());
			log.debug("Error Stack Trace:",ex);
			log.fatal("System Exit");
		}

		log.info("DONE!!!" );
		
	}


}
