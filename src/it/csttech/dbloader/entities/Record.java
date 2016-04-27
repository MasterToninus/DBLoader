package it.csttech.dbloader.entities;

import java.util.Date;

/**
 * Javabean record.
 *
 *
 * @author drago-orsone, MasterToninus
 * @since mm-dd-yyyy
 * @see <a href="https://en.wikipedia.org/wiki/JavaBeans"> JavaBeans <\a>
 * @see <a href="http://javarevisited.blogspot.it/2014/05/why-use-serialversionuid-inside-serializable-class-in-java.html" >Serial Version (???) <\a>
 */
public class Record implements java.io.Serializable {

	private static final long serialVersionUID = 2L;

	private int id;
	private String name;
	private Date birthday;
	private double height;
	private boolean married;

  public int getId() {
    return this.id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Date getBirthday() {
    return this.birthday;
  }
  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }
	public double getHeight() {
    return this.height;
  }
  public void setHeight(double height) {
    this.height = height;
  }
  // Diversa sintassi per gli attributi boolean ( 'is' al posto di 'get' )
  public boolean isMarried() {
    return this.married;
  }
  public void setMarried(boolean married) {
    this.married = married;
  }

  @Override
  public String toString(){
		return String.format("[%1$d, %2$s, %3$td/%3$tm/%3$tY, %4$.3f, %5$s]" ,this.id, this.name, this.birthday, this.height, this.married);
  }

  @Override
  public boolean equals(Object o){
		if (o == this)
			return true;
		if (!(o instanceof Record))
			return false;
		Record rec = (Record) o; //cast
		if(
	    this.id == rec.getId() &&
	    this.name == rec.getName() &&
	    this.birthday == rec.getBirthday() &&
	    this.height == rec.getHeight() &&
	    this.married == rec.isMarried())
			return true;
		else
			return false;
  }

	@Override
	public int hashCode() {
		return this.toString().hashCode() ;
	}

  @Override //parametro di ritorno non controllato dal compilatore -> posso overridare
  public Record clone(){ //return object
		Record record = new Record();
		record.setId(this.id);
		record.setName(this.name);
		record.setBirthday(this.birthday);
		record.setHeight(this.height);
		record.setMarried(this.married);
		return record; //cast to object
  }

}
