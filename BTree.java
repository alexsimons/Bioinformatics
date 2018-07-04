import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BTree {

	int deg;
	int seqLength;
	BTreeNode root;
	File treeFile;
	RandomAccessFile raf;
	BTCache cache;
	KeyCoder kc = new KeyCoder();
	boolean cacheOption;

	public BTree() throws IOException {
		cacheOption = ArgsCreate.useCache;
		
		if (cacheOption) {
			cache = new BTCache(ArgsCreate.cacheSize);
		}
		deg = ArgsCreate.degree;
		seqLength = ArgsCreate.sequenceLength;

		File metadata = new File(ArgsCreate.fileName + ".btree.metadata." + seqLength + "." + deg);

		raf = new RandomAccessFile(metadata, "rw");
		raf.writeInt(deg);
		raf.writeInt(seqLength);
		raf.close();

		root = new BTreeNode(deg, 0);
		treeFile = new File(ArgsCreate.fileName + ".btree.data." + seqLength + "." + deg);
		diskWrite(root);
	}

	public BTree(File tFile, File mFile, boolean cacheArg) throws IOException {
		cacheOption = cacheArg;
		if (cacheOption) {
			cache = new BTCache(ArgsSearch.cacheSize);
		}
		
		raf = new RandomAccessFile(mFile, "r");
		this.deg = raf.readInt(); 
		this.seqLength = raf.readInt();
		raf.close();

		treeFile = tFile;
		raf = new RandomAccessFile(treeFile, "r");
		root = diskRead(0);
		raf.close();
	}

	public void insert(TreeObject to) {
		BTreeNode r = root;
		
		if (r.numKeys == 2 * deg - 1) {
			BTreeNode s = new BTreeNode(deg, getFileLength());
			diskWrite(s);
			root.offset = getFileLength();
			diskWrite(root);
			root = s;
			s.isLeaf = false;
			s.numKeys = 0;
			s.kidPtrs[0] = r.offset;
			s.offset = 0;
			splitChild(s, 0, r);
			insertNonFull(s, to);
		} else {
			insertNonFull(root, to);
		}
	}

	public void insertNonFull(BTreeNode paramNode, TreeObject to) {
		int i = paramNode.numKeys - 1;
		
		if (paramNode.isLeaf) {
			while (i >= 0 && to.objKey < paramNode.nodeKeys[i].objKey) {
				paramNode.nodeKeys[i + 1] = new TreeObject(paramNode.nodeKeys[i].objKey, paramNode.nodeKeys[i].objFreq);
				i--;
			}
			paramNode.nodeKeys[i + 1] = to;
			paramNode.numKeys++;
			nodeWrite(paramNode);
		} else {
			while (i >= 0 && to.objKey < paramNode.nodeKeys[i].objKey) {
				i--;
			}
			i++;

			BTreeNode c;
			if (paramNode.kidPtrs[i] != -1) {
				c = diskRead(paramNode.kidPtrs[i]);

				if (c.numKeys == 2 * deg - 1) {
					splitChild(paramNode, i, c);
					if (to.objKey > paramNode.nodeKeys[i].objKey) {
						i++;
					}
				}
				insertNonFull(diskRead(paramNode.kidPtrs[i]), to);
			}
		}
	}

	public BTreeNode search(BTreeNode node, long key) {
		int i = 0;
		BTreeNode retNode = null;
		while (i < node.numKeys && key > node.nodeKeys[i].objKey) {
			i++;
		}
		if (i < node.numKeys && key == node.nodeKeys[i].objKey) {
			return node;
		}
		if (node.isLeaf) {
			return null;
		}
		if (node.kidPtrs[i] != -1) {
			retNode = diskRead(node.kidPtrs[i]);
		}
		return search(retNode, key);
	}

	public void splitChild(BTreeNode x, int i, BTreeNode y) {
		BTreeNode z = new BTreeNode(deg, getFileLength());
		z.isLeaf = y.isLeaf;
		z.numKeys = deg - 1;
		diskWrite(z);
		
		for (int j = 0; j < deg - 1; j++) {			
			z.nodeKeys[j] = new TreeObject(y.nodeKeys[j + deg].objKey, y.nodeKeys[j + deg].objFreq);
			y.nodeKeys[j + deg] = new TreeObject();
		}
		if (!y.isLeaf) {
			for (int j = 0; j < deg; j++) {
				z.kidPtrs[j] = y.kidPtrs[j + deg];
				y.kidPtrs[j + deg] = -1L;
			}
		}

		y.numKeys = deg - 1;
		for (int j = x.numKeys; j > i; j--) {
			x.kidPtrs[j + 1] = x.kidPtrs[j];
			x.kidPtrs[j] = -1L;
		}

		x.kidPtrs[i + 1] = z.offset;

		for (int j = x.numKeys - 1; j > i - 1; j--) {
			x.nodeKeys[j + 1] = new TreeObject(x.nodeKeys[j].objKey, x.nodeKeys[j].objFreq);
		}

		x.nodeKeys[i] = new TreeObject(y.nodeKeys[deg - 1].objKey, y.nodeKeys[deg - 1].objFreq);
		y.nodeKeys[deg - 1] = new TreeObject();
		x.numKeys = x.numKeys + 1;

		nodeWrite(z);
		nodeWrite(y);
		nodeWrite(x);
	}

	public void nodeWrite(BTreeNode node) {
		if (cache != null) {
			BTreeNode checkNode = cache.add(node);
			if (checkNode != null) {
				diskWrite(checkNode);
			}
		} else {
			diskWrite(node);
		}
	}

	public void diskWrite(BTreeNode node) {
		try {
			raf = new RandomAccessFile(treeFile, "rw");
			raf.seek(node.offset);
			for (int i = 0; i < node.nodeKeys.length; i++) {
				raf.writeLong(node.nodeKeys[i].objKey);
				raf.writeInt(node.nodeKeys[i].objFreq);
			}
			for (int i = 0; i < node.kidPtrs.length; i++) {
				raf.writeLong(node.kidPtrs[i]);
			}
			raf.writeInt(node.numKeys);
			raf.writeBoolean(node.isLeaf);
			raf.writeLong(node.offset);
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BTreeNode diskRead(long filePos) {

		BTreeNode checkCache = null;
		if (cache != null) {
			checkCache = cache.getObject(filePos);
		}
		if (checkCache != null) {
			return checkCache;
		}

		BTreeNode node = new BTreeNode(deg, filePos);
		try {
			raf = new RandomAccessFile(treeFile, "r");
			raf.seek(filePos);
			for (int i = 0; i < node.nodeKeys.length; i++) {
				node.nodeKeys[i].objKey = raf.readLong();
				node.nodeKeys[i].objFreq = raf.readInt();
			}
			for (int i = 0; i < node.kidPtrs.length; i++) {
				node.kidPtrs[i] = raf.readLong();
			}
			node.numKeys = raf.readInt();
			node.isLeaf = raf.readBoolean();
			node.offset = raf.readLong();
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return node;
	}

	private long getFileLength() {
		long fileLength = -1L;
		try {
			raf = new RandomAccessFile(treeFile, "r");
			fileLength = raf.length();
			raf.close();
		} catch (IOException e) {
			System.out.println("Error accessing file");
			e.printStackTrace();
		}
		return fileLength;
	}

	public void writeCache() {
		System.out.println("writing cache!");//I've got to go when finished
		for (int i = cache.getSize(); i > 0; i--) {
			diskWrite(cache.removeLast());
		}
	}
}