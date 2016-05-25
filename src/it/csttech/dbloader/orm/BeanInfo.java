package it.csttech.dbloader.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import java.util.*;
//import java.lang.annotation.Annotation;

import it.csttech.dbloader.entities.Getter;
import it.csttech.dbloader.entities.Setter;
import it.csttech.dbloader.entities.Column;
import it.csttech.dbloader.entities.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BeanInfo{

  static final Logger log = LogManager.getLogger(BeanInfo.class.getName());

  private final Class<?> clazz;
  private final String clazzName;
  private HashMap<String, Method> getters;
  private HashMap<String, Method> setters;
  private final HashMap<String, FieldInfo> fieldInfoMap;
  private String tableName;
  private String insertQuery;
  private String createTableQuery;
  //TODO private String varieQuery;
  

  /**
   * [BeanInfo description]
   * @param  clazz [description]
   * @return       [description]
   */
  public BeanInfo(Class<?> clazz) {
    this.clazz = clazz;
    this.clazzName = clazz.getName();
    this.tableName = clazz.getAnnotation(Entity.class).tableName();
    fieldInfoMap = fillFields(clazz.getDeclaredFields());
    try{
    	
      	fillMethods();
      	generateQueries();
    } catch(Exception e ){
      //Questa eccezione di metodo non trovato secondo me va tradotta in una beaninfo exception (annotation dice che il field è un setter/getter ma il corrispettivo metodo non c'è)
      //Credo che tutte le exception di reflection andrebbero wrappate in un'eccezzione di beaninfo.
      e.printStackTrace();
    }
  }

  private HashMap<String, FieldInfo> fillFields(Field[] allFields) {

	HashMap<String, FieldInfo> fieldInfoMap = new HashMap<String, FieldInfo>();
	for (Field f : allFields)
		if (f.isAnnotationPresent(Column.class))
			fieldInfoMap.put(f.getName(), new FieldInfo(f));
	return fieldInfoMap;
  }

  private void fillMethods() throws NoSuchMethodException {
    getters = new HashMap<String,Method>();
    setters = new HashMap<String,Method>();
    for (String key : fieldInfoMap.keySet()) {
      FieldInfo f = fieldInfoMap.get(key);
      String name = f.getFieldName();
      if(f.isSetter()) {
        StringBuilder methodName = new StringBuilder("set"); // è consigliabile usare stringbuilder se non si hanno thread concorrenti
        methodName.append(name.substring(0, 1).toUpperCase());
        methodName.append(name.substring(1).toLowerCase());
        setters.put(name.toLowerCase(), clazz.getMethod(methodName.toString(), f.getType()));
      }
      if(f.isGetter()){
        StringBuilder methodName = new StringBuilder();
        if(f.getType().isAssignableFrom(Boolean.TYPE)) methodName.append("is");
        else methodName.append("get");
        methodName.append(name.substring(0, 1).toUpperCase());
        methodName.append(name.substring(1).toLowerCase());
        getters.put(name.toLowerCase(), clazz.getMethod(methodName.toString(), (Class<?>[]) null)); //finezza cast superfluo
      }
    }
  }

  private void generateQueries() {
	generateCreateTableQuery();
	generateInsertRecordQuery();	
  }

  private void generateCreateTableQuery() {
	StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (" );
	for (String key : fieldInfoMap.keySet()) {
		FieldInfo f = fieldInfoMap.get(key);
		if (f.isAutoIncrement() && f.getTypeName().equals("INT"))
			createTableQuery.append(" " + f.getColumnName() + " " +  "SERIAL");
		else
			createTableQuery.append(" " + f.getColumnName() + " " +  f.getTypeName());
		if (f.isNotNull())
			createTableQuery.append(" NOT NULL");
		if (f.isPrimaryKey())
			createTableQuery.append(" PRIMARY KEY");
		createTableQuery.append(",");
	}
	createTableQuery.deleteCharAt(createTableQuery.length()-1);
	createTableQuery.append(" )");
	this.createTableQuery = createTableQuery.toString();
  }

  private void generateInsertRecordQuery() {
	StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName + " (" );
	//int numberOfFields = fieldInfoMap.size();
	int fieldCounter = 0;
	for (String key : fieldInfoMap.keySet()) {
		FieldInfo f = fieldInfoMap.get(key);
		if (!(f.isAutoIncrement() && f.getTypeName().equals("INT"))) {
			insertQuery.append(" " + f.getColumnName() + ",");
			fieldCounter++;
		}
	}
	insertQuery.deleteCharAt(insertQuery.length()-1);
	insertQuery.append(" ) VALUES (");
	for (int i = 0; i < fieldCounter; i++)
		insertQuery.append(" ?,");
	insertQuery.deleteCharAt(insertQuery.length()-1);
	insertQuery.append(")");
	this.insertQuery = insertQuery.toString();
  }


  public void test(){
    System.out.println(createTableQuery + "\n" + insertQuery + "\n");
    for (String key : this.getters.keySet()) {
      System.out.format("Key = %s \t %s \t %s %n",key ,getters.get(key).getName(), setters.get(key).getName() );
    }
  }

  public HashMap<String,Method> getSetters(){
	  return setters;
  }

  public HashMap<String,Method> getGetters(){
	  return getters;
  }

  public String getClassName() {
	 return clazzName;
  }

  //Restituisco object. dopo andrà castato!
  public Object getInstance() throws java.lang.InstantiationException, java.lang.IllegalAccessException {
    return clazz.newInstance();
  }

/**
 * @return the tableName
 */
public String getTableName() {
	return tableName;
}

/**
 * @return the insertQuery
 */
public String getInsertQuery() {
	return insertQuery;
}

/**
 * @return the createTableQuery
 */
public String getCreateTableQuery() {
	return createTableQuery;
}

public Set<String> getFieldKeySet() {
	return fieldInfoMap.keySet();
}

public HashMap<String, FieldInfo> getFieldInfoMap() {
	return fieldInfoMap;
}

}

class BeanException extends Exception {
  private static final long serialVersionUID = 1L;
}
