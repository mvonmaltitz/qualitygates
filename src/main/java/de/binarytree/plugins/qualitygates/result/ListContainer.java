package de.binarytree.plugins.qualitygates.result;

import java.util.LinkedList;
import java.util.List;

abstract class ListContainer<T>{
	
	protected List<T> items = new LinkedList<T>(); 

	protected List<T> getItems(){
		return this.items; 
	}
	protected void addOrReplaceItem(T item) {
		int index = getIndexOfItem(item);
		if (index == -1) {
			this.items.add(item);
		} else {
			this.replaceItemAtIndex(index, item);
		}
	}
	private int getIndexOfItem(T item){
		
		for(int i = 0; i < this.items.size(); i++){
			if(this.isSameItem(this.items.get(i), item)){
				return i; 
			}
		}
		return -1; 
	}
	private void replaceItemAtIndex(int index, T item){
		this.items.remove(index); 
		this.items.add(index, item); 
	}
	protected abstract boolean isSameItem(T a, T b); 
		
}