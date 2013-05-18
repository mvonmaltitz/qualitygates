package model;


public class Address{
	private String street;
	private int plz;
	private String city;

	public Address(String street, int plz, String city) {
		this.street = street; 
		this.plz = plz; 
		this.city = city; 
	}
	
	public String getStreet() {
		return this.street; 
	}
	public int getPLZ() {
		return this.plz; 
	}
	public String getCity() {
		return this.city; 
	}
}
