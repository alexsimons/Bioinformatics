
public class BTreeNode {
	
	public long offset;
	public int numKeys;
	public boolean isLeaf;
	public TreeObject[] nodeKeys;
	public long[] kidPtrs;

	public BTreeNode(int deg, long local) {
		offset = local;
		numKeys = 0;
		isLeaf = true;
		int maxKeys = 2 * deg -1;
		int maxKids = maxKeys + 1;
		long defaultVal = -1;
		nodeKeys = new TreeObject[maxKeys];			
		kidPtrs = new long[maxKids];

		for(int i = 0; i < maxKeys; i++) {
			nodeKeys[i] = new TreeObject();
		}		
		for(int i = 0; i < maxKids; i++) {
			kidPtrs[i] = defaultVal;
		}
	}

	public boolean equals(BTreeNode t) {
		return offset == t.offset;
	}
}
