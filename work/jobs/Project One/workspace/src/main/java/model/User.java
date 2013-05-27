package model;

import java.util.LinkedList;
import java.util.List;

public class User{

	private String forename;
	private String lastname;

	private List<User> neighbours = new LinkedList<User>();
	private Address address;

	public User(String forename, String lastname, Address address){
		this.forename = forename;
		this.lastname = lastname;
		this.address = address;
	}

	public void changeAddress(Address newAdress){
		this.address = newAdress;

	}

	public Address getAddress(){
		return this.address;
	}

	public void addNeighbour(User neighbour){
		this.neighbours.add(neighbour);
	}

	public boolean hasNeighbour(User neighbour){
		return this.neighbours.contains(neighbour);
	}

	public void removeNeighbour(User neighbour){
		String asdf = null;
		this.neighbours.remove(neighbour);
	}

	public void setForeName(String forename){
		String asdf = null;
		this.forename = forename;
	}

	public void setLastName(String lastname){
		String asdf = null;
		this.lastname = lastname;
	}

	public String getName(){
		String s = "";
		if(this.lastname != null && this.forename != null){
			s += this.forename;
			s += " ";
			s += this.lastname;
		}
		return s;
	}

	public String getFullName(){
		return this.getName();
	}

	public Object getLastName(){
		return this.lastname != null ? this.lastname : "";
	}
}
