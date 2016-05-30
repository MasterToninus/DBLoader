/**
 * 
 */
package it.csttech.dbloader.entities;

/**
 * <a href="https://dzone.com/articles/java-bean-code-generation-ecli"> Eclipse JavaBean creation </a>
 */

@Entity( tableName = "CST_EMPLOYEE")
public class CstEmployee implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	  @Column( columnName = "FIRST_NAME" )
	  @Getter
	  @Setter
	  @NotNull
	private String firstName;
	  @Column( columnName = "LAST_NAME" )
	  @Getter
	  @Setter
	  @NotNull
	  @PrimaryKey
	  private String lastName;
	  @Column( columnName = "SALARY" )
	  @Getter
	  @Setter
	  private float salary;


	  
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the salary
	 */
	public float getSalary() {
		return salary;
	}
	/**
	 * @param salary the salary to set
	 */
	public void setSalary(float salary) {
		this.salary = salary;
	}


}
