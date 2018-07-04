import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GeneBankSearch {

	static String btreeFName, queryFName, metaFName;
	static int seqLen, debugLevel;
	static long focus;//key to search
	static BTreeNode nodeWithVal;
	static BTree tree;
	static KeyCoder kc;
	static QueryReader qr;
	static long startTime;

	public static void main(String args[]) {
		if (!ArgsSearch.validate(args)) {
			System.err.println("USAGE: java GeneBankSearch <0/1 (no/with Cache)> <btree file>"
					+ "<query file> [<cache size>] [<debug level>]");
		}
		getSome();
	}

	public static void getSome() {

		btreeFName = ArgsSearch.btreeFile.getName();
		metaFName = btreeFName.replaceAll("data", "metadata");
		queryFName = ArgsSearch.queryFile.getName();
		seqLen = Integer.parseInt(btreeFName.split("\\.")[4]);
		char qLen = queryFName.charAt(queryFName.length() - 1);
		kc = new KeyCoder();
		qr = new QueryReader();
		ArrayList<String> fromQuery;

		try {
			// verify sequence lengths are the same
			if (seqLen != Character.getNumericValue(qLen)) {
				System.err.println("The BTree file and query file must have same sequence length.");
				System.err.println("very useful usage here!");
			}
			// BTree from file constructor checks for cache option.
			System.err.println("Building BTree to Search ...");
			startTime = System.currentTimeMillis();
			fromQuery = new ArrayList<>();
			tree = new BTree(new File(btreeFName), new File(metaFName), ArgsSearch.useCache);			
			String seq;

			while ((seq = qr.next()) != null) {
				fromQuery.add(seq);
			}

			for (String s : fromQuery) {
				focus = kc.encodeKey(s);
				if ((nodeWithVal = tree.search(tree.root, focus)) != null) {
					for (TreeObject to : nodeWithVal.nodeKeys) {
						if (to.objKey == focus) {
							System.out.println(kc.decodeKey(to.objKey, seqLen) + ": " + to.objFreq);
						}
					}
				}
			}
			
			System.err.println("Time: " + (System.currentTimeMillis() - startTime) * .001 + " secs.");

		} catch (IOException e) {
			System.err.println("problem in GeneBankSearch!");
			e.printStackTrace();
		}
	}

}
