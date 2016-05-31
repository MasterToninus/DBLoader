package it.csttech.dbloader.entities;

import java.io.Serializable;

@Entity( tableName = "ADDRESSES")
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column( columnName = "STREET" )
	  @Getter
	  @Setter
	  @NotNull
	private String street;
	  @Column( columnName = "NUMBER" )
	  @Getter
	  @Setter
	  @NotNull
	  private int number;

	  @Column( columnName = "CITY" )
	  @Getter
	  @Setter
	  @NotNull  
	private String city;
	  @Column( columnName = "POSTALCODE" )
	  @Getter
	  @Setter
	  @NotNull
	private int postalCode;
	  @Column( columnName = "COUNTRY" )
	  @Getter
	  @Setter
	  @NotNull
	private String country;
	/**
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}
	/**
	 * @param street the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}
	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the postalCode
	 */
	public int getPostalCode() {
		return postalCode;
	}
	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
}
