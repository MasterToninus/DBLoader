package it.csttech.dbloader.orm;

import java.lang.reflect.Field;
import it.csttech.dbloader.entities.*;


public class FieldInfo{


  /**
   * Attributi estraibili dall'oggetto Field
   */
  private final String fieldName;
  private final Class<?> type;

  /**
   * Attributi legati al comando sql.
   */
  private final String columnName;
  private final String typeName;
  private final boolean getter;
  private final boolean setter;
  private final boolean primaryKey;
  private final boolean notNull;
  private final boolean autoIncrement;

  /**
   * [FieldInfo description]
   * @return [description]
   */
  public FieldInfo(Field field) throws java.sql.SQLException {
    this.fieldName = field.getName();
    this.type = field.getType();

    this.columnName = field.getAnnotation(Column.class).columnName();
    this.typeName = ojb(this.type);
    this.getter = field.isAnnotationPresent(Getter.class);
    this.setter = field.isAnnotationPresent(Setter.class);
    this.primaryKey = field.isAnnotationPresent(PrimaryKey.class);
    this.notNull = field.isAnnotationPresent(NotNull.class);
    this.autoIncrement = field.isAnnotationPresent(AutoIncrement.class);

  }

  public String getFieldName(){
    return this.fieldName;
  }
  public Class<?> getType(){
    return this.type;
  }
  public String getColumnName(){
    return this.columnName;
  }
  public String getTypeName(){
    return this.typeName;
  }
  public boolean isGetter(){
    return getter;
  }
  public boolean isSetter(){
    return setter;
  }

  public boolean isPrimaryKey() {
	return primaryKey;
  }

  public boolean isNotNull() {
	return notNull;
  }

  public boolean isAutoIncrement() {
	return autoIncrement;
  }

  /**
   * Facciamo a mano una cosa che poi faremo in automatico (andrebbe estesa molto)
   * 	ObJectRelationalBridge
   * @param  klazz [description]
   * @return string sql relativo al tipo di variabile [description]
   * @see <a href=https://db.apache.org/ojb/> link<\a>
   */
  private static String ojb(Class<?> klazz) throws java.sql.SQLException {		//Object Relational Bridge - make conversion from java to postgres types.
	if(klazz.equals(String.class)) return java.sql.JDBCType.VARCHAR.getName();
//    else if(klazz.isPrimitive() && klazz.getName().toLowerCase().equals("double")) return java.sql.JDBCType.DOUBLE.getName();
    else if(klazz.isPrimitive()) return klazz.getName().toUpperCase();
    else if(klazz.equals(Boolean.class)) return java.sql.JDBCType.BOOLEAN.getName();
    else if(klazz.equals(Integer.class)) return java.sql.JDBCType.INTEGER.getName();
    else if(klazz.equals(Long.class)) return java.sql.JDBCType.BIGINT.getName();
    else if(klazz.equals(Float.class)) return java.sql.JDBCType.FLOAT.getName();
    else if(klazz.equals(Double.class)) return java.sql.JDBCType.DOUBLE.getName();	
    else if(klazz.equals(java.sql.Date.class)) return java.sql.JDBCType.DATE.getName();
    else throw new java.sql.SQLException("Can't infer the SQL type to associate to " + klazz);
  }

}
