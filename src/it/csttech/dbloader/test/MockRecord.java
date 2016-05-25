package it.csttech.dbloader.test;

import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import it.csttech.dbloader.orm.BeanInfo;
import it.csttech.dbloader.entities.Record;

/**
 * Preliminary version!
 *	(WIP)
 * 
 */
public class MockRecord {
	private final BeanInfo beanInfo;
	private Random random = new Random();
	// Map wich associate to each setters its nextrandom method (
	private final Map<Method, RandomOperation> setterRandomMap;
	// l'estensione all' extract di file dovrebbe fare per ogni colonna un
	// parse.
	// sembra un'associazione funzionale colonna <-> metodo per queste cose devo
	// fare lo pseudo paradigma funzionale con lambda o classi con unico metodo
	// (vedi tutorial)
	// non voglio testare ogni volta il tipo da settare!

	public MockRecord(BeanInfo beanInfo) throws Exception{
		this.beanInfo = beanInfo;
		setterRandomMap = generateMapping();

	}

	/**
	 * voglio generare un mapping da metodi a metodi. ad ogni setter voglio
	 * associare un metodo di random che genera il giusto argument in modo
	 * random
	 *  //TODO: specify exception
	 * @return
	 */
	private Map<Method, RandomOperation> generateMapping() throws Exception {
		List<Method> setterList = new ArrayList<Method>(beanInfo.getSetters().values());
		Map<Method, RandomOperation> mapping = new HashMap<Method, RandomOperation>();
		for (Method m : setterList) {
			Class<?> argType = m.getParameterTypes()[0];
			mapping.put(m, objRelation(argType));
		}
		return mapping;
	}

	public Object next() {
		Object record = null ;
		try{
			record = beanInfo.getInstance();
		for (Method m : setterRandomMap.keySet())
			m.invoke(record, setterRandomMap.get(m).next());
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return record;
	}

	/**
	 * Discarded stupid non functional method
	 *  //TODO: specify exception
	 * @param clazz
	 * @return
	 */
	private Object RandomObject(Class<?> clazz) throws Exception{
		if (clazz.equals(boolean.class)) {
			return random.nextBoolean();
		} else if (clazz.equals(int.class)) {
			return random.nextInt();
		} else if (clazz.equals(long.class)) {
			return random.nextLong();
		} else if (clazz.equals(float.class)) {
			return random.nextFloat();
		} else if (clazz.equals(double.class)) {
			return random.nextDouble();
		} else if (clazz.equals(Date.class)) {
			return new Date(random.nextLong());
		} else
			throw new Exception();
	}

	private RandomOperation objRelation(Class<?> clazz) throws Exception {
		System.out.println(clazz);
		if ( clazz.equals(boolean.class)){
			return  () -> random.nextBoolean();
		}
		else if ( clazz.equals(int.class)){
			return  () -> random.nextInt();
		}
		else if ( clazz.equals(long.class)){
			return  () -> random.nextLong();
		}
		else if (clazz.equals(float.class)){
			return  () -> random.nextFloat();
		}
		else if (clazz.equals(double.class)){
			return  () -> random.nextDouble();
		}
		else if(clazz.equals(Date.class)){
			return () -> new Date(random.nextLong());
		}
		else if(clazz.equals(String.class)){
			return () -> {
				char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 20; i++) {
				    char c = chars[random.nextInt(chars.length)];
				    sb.append(c);
				}
				return sb.toString();
			};
		}
			else throw new Exception(); //TODO: specify exception
	}

	public static void main(String[] args) {
		try {
			BeanInfo beanInfo = new BeanInfo(Record.class);
			MockRecord mockRecord = new MockRecord(beanInfo);
			System.out.println(mockRecord.next());
			
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ------------------------
	// Ugly Naive functional part
	// ------------------------
	interface RandomOperation {
		public Object next();
	}

}
