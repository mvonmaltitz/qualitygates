package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UserTest{

	private User marcel;
	private User max;

	@Before
	public void setUp() throws Exception{
		Address address = new Address("Reutinerstraße 5", 88131, "Lindau"); 
		this.marcel = new User("Marcel", "von Maltitz", address);

		this.max = new User("Max", "Heinrich", new Address( "XYZ 3", 80333, "München"));
	}

	@Test
	public void testGetLastNameWhenEmpty(){
		this.marcel.setLastName(null);
		assertEquals(this.marcel.getLastName(), "");
	}

	@Test
	public void testGetLastNameWhenNotEmpty(){
		assertEquals(this.marcel.getLastName(), "von Maltitz");
	}

	@Test
	public void testGetFullnameSuccessfullWhenForeandLastNamespecified(){
		assertEquals(this.marcel.getName(), "Marcel von Maltitz");
		assertEquals(this.marcel.getFullName(), "Marcel von Maltitz");
	}

	@Test
	public void testGetFullNameEmptyWhenForeNameEmpty(){
		this.marcel.setForeName(null);
		assertEquals(this.marcel.getName(), "");
	}

	@Test
	public void testGetFullNameEmptyWhenLastNameEmpty(){
		this.marcel.setLastName(null);
		assertEquals(this.marcel.getName(), "");
	}

	@Test
	public void testAddingNeighbourSavesGivenUserasNeighbour(){
		assertFalse("Neighbourhood already defined", this.marcel.hasNeighbour(this.max));
		addNeighbour(marcel, max); 
		assertTrue("Adding neighbour failed", this.marcel.hasNeighbour(this.max));
	}
	@Test 
	public void testRemovingExistentNeighbour() {
		addNeighbour(marcel, max); 
		removeNeighbour(marcel, max); 
		assertFalse(marcel.hasNeighbour(max)); 
	}
	private void removeNeighbour(User user, User neighbour){
		user.removeNeighbour(neighbour); 
		
	}

	private void addNeighbour(User user, User neighbour) {
		user.addNeighbour(neighbour); 
	}
}
