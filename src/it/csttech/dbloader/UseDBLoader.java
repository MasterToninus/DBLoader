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

*
* @author drago-orsone, MasterToninus
*
*/
public class UseDBLoader {

	static final Logger log = LogManager.getLogger();

	public static void main(String[] args) {

		Properties prop = readProperties(System.getProperty("prop.File"));
		Orm orm = null;

		try {
			orm = Orm.getInstance(prop.getProperty("orm.config"));
			BeanInfo beanInfo = orm.getBeanInfo(prop.getProperty("bean.class"));

			MockRecord mockRecord = new MockRecord(beanInfo);
			for (int j = 0; j < 50; j++) {
				orm.save(mockRecord.next());
			}
			
			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.CstEmployee");
			mockRecord = new MockRecord(beanInfo);

			for (int j = 0; j < 50; j++) {
				orm.save(mockRecord.next());
			}

			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.Address");
			mockRecord = new MockRecord(beanInfo);
			for (int j = 0; j < 50; j++) {
				orm.save(mockRecord.next());
			}			

			
			BeanBuilder bb = new BeanBuilder();
			bb.init("RunTime");
			bb.addField("id", Integer.class, false, true, true, false);
			bb.addField("name", String.class, false, true, true, false);
			Class<?> clazz = bb.load();
			orm.addBeanClass(clazz);
			beanInfo = orm.getBeanInfo(clazz);
			mockRecord = new MockRecord(beanInfo);
			for (int j = 0; j < 50; j++) {
				orm.save(mockRecord.next());
			}			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (orm != null)
				orm.destroy();
		}
	}



	public static Properties readProperties(String propFile) {
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
