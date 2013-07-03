package de.binarytree.plugins.qualitygates.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ListContainerTest {

	class ListContainerImpl<T> extends ListContainer<T> {
		public List<T> getList() {
			return this.getItems();
		}

		public void addItem(T s) {
			this.addOrReplaceItem(s);
		}

		@Override
		protected boolean isSameItem(T a, T b) {
			return a.equals(b);
		}
	}

	private ListContainerImpl<String> container;
	private String string;

	@Before
	public void setUp() throws Exception {
		container = new ListContainerImpl<String>() {
			@Override
			protected boolean isSameItem(String a, String b) {
				// Using plain equality means we cannot test if a replacement as
				// really occurred
				return a.substring(0, 4).equals(b.substring(0, 4));
			}
		};
		string = "Test String";
	}

	@Test
	public void testAddItem() {
		container.addItem(string);
		assertTrue(container.getList().contains(string));
	}

	@Test
	public void testAddDuplicateItem() {

		container.addItem(string);
		container.addItem(string);
		assertTrue(container.getList().contains(string));
		assertEquals(1, container.getList().size());
	}

	@Test
	public void testAddDuplicateItemWithContext() {

		this.addArbitraryString(7, "before");
		container.addItem(string + "old");
		this.addArbitraryString(7, "after");
		container.addItem(string + "new");
		assertTrue(container.getList().contains(string + "new"));
		assertFalse(container.getList().contains(string + "old"));

	}

	private void addArbitraryString(int count, String prefix) {
		for (int i = 0; i < count; i++) {
			container.addItem(prefix + " " + i);
		}
	}

}
