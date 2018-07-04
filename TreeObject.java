public class TreeObject implements Comparable<TreeObject>{
	
	public long objKey;
	public int objFreq;
	
	public TreeObject(long key, int freq) {
		objKey = key;
		objFreq = freq;
	}
	
	public TreeObject() {
		objKey = -1;
		objFreq = 0;
	}
	
	public long getKey() {
		return objKey;
	}

	@Override
	public String toString() {
		return "TreeObject [key=" + objKey + ", freq=" + objFreq + "]";
	}
	
	@Override
	public int compareTo(TreeObject o) {
		return (int) (objKey - o.objKey);
	}
}
