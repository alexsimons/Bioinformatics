import java.util.LinkedList;

public class BTCache { 	
	private LinkedList<BTreeNode> cache;	
	private final int SIZE; 
	
	public BTCache(int size) {		
		cache = new LinkedList<BTreeNode>();
		SIZE = size;
	}

	public BTreeNode add(BTreeNode node) {
		BTreeNode retVal = null;
		if (isFull()) {
			 retVal = cache.removeLast();
		}
		BTreeNode moveToFront = findNode(node);
		if (moveToFront == null){
			cache.addFirst(node);
		}
		else {
			cache.addFirst(moveToFront);
		}
		return retVal;
	}

	public BTreeNode findNode(BTreeNode node) {		
		for (int i = 0; i < cache.size(); i++) {
			if (cache.get(i).equals(node)) {
				return cache.remove(i);
			}
		}
		return null;
	}
	
	public boolean isFull() {
		return cache.size() == SIZE;
	}

	public BTreeNode getObject(long fileOffset) {
		for (int i = 0; i < cache.size(); i++) {
			if (cache.get(i).offset == fileOffset) {
				BTreeNode toReturn = cache.remove(i);
				cache.addFirst(toReturn);
				return toReturn;
			}
		}		
		return null;
	}

	public BTreeNode removeLast () {
		return cache.removeLast();
	}

	public int getSize() {
		return cache.size();
	}
}//end BTCache