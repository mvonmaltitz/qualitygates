package de.binarytree.plugins.qualitygates.result;

import java.util.LinkedList;
import java.util.List;

/**
 * This class implements the functionality to hold a list of unique items.
 * 
 * @author Marcel von Maltitz
 * 
 * @param <T>
 *            the type of the items to be held
 */
abstract class ListContainer<T> {

    private List<T> items = new LinkedList<T>();

    /**
     * Returns the items of this container.
     * 
     * @return the items of this container
     */
    protected List<T> getItems() {
        return this.items;
    }

    /**
     * Adds a item to this container. If the item is already contained, it is
     * replaced at its former index.
     * 
     * @param item
     *            the item to be added
     */
    protected void addOrReplaceItem(T item) {
        int index = getIndexOfItem(item);
        if (index == -1) {
            this.items.add(item);
        } else {
            this.replaceItemAtIndex(index, item);
        }
    }

    private int getIndexOfItem(T item) {

        for (int i = 0; i < this.items.size(); i++) {
            if (this.isSameItem(this.items.get(i), item)) {
                return i;
            }
        }
        return -1;
    }

    private void replaceItemAtIndex(int index, T item) {
        this.items.remove(index);
        this.items.add(index, item);
    }

    /**
     * Whether or not the objects are the same.
     * 
     * When adding an element, which is, defined by this method, the same as an
     * already added element, the old element is replaced.
     * 
     * @param a
     *            the one element
     * @param b
     *            the other element
     * @return whether they are the same
     */
    protected abstract boolean isSameItem(T a, T b);

}