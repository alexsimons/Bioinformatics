import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Creates a BTree from a given GeneBank file and sends the DNA sequences to the tree.
 * 
 * @authors Garrett Smith
 *
 */

public class GeneBankCreateBTree {
	private ArrayList<TreeObject> theObjs;
	private BTree bTree;
	private ParseGBK parser;
	private long startTime;
	private int totalInserts;
	private KeyCoder kc;

	public static void main(String[] args) throws IOException {
		if(!ArgsCreate.validate(args)) {
			System.err.println("Arguments are askew closing..");
		}
		GeneBankCreateBTree gbc = new GeneBankCreateBTree();
		gbc.getSome();
	}
	
	public void getSome() throws IOException {
		parser = new ParseGBK();
		bTree = new BTree();
		totalInserts = 0;

		theObjs = parser.Extract();
		addSequences();
		tidyUp();
	}
	
	public void addSequences() {
		startTime = System.currentTimeMillis();
		System.err.printf("\nBuilding BTree from: %s ...\n", ArgsCreate.fileName);
		
		for(TreeObject to : theObjs) {
			totalInserts += to.objFreq;
			bTree.insert(to);
		}
		
	}
	
	public void tidyUp() throws IOException {
		if(ArgsCreate.useCache) {
			bTree.writeCache();
		}
		System.err.printf("\nFinished building BTree.\n", ArgsCreate.fileName);
		System.err.printf("\n%d sequences were inserted into the tree.\n", totalInserts);
		System.err.println("Approx time to build tree: " + (System.currentTimeMillis() - startTime) * .001 + " secs");
		
		if(ArgsCreate.debugLevel == 1) {
			System.err.println("writing dump file ...");
			writeDump();
		}
	}
	
	private void writeDump() {
		String title = "dump";
		PrintWriter dumpWriter = null;
		kc = new KeyCoder();

		try {
			dumpWriter = new PrintWriter(title);
		} catch (Exception e) {
			System.err.println("problem with dump file.");
			e.printStackTrace();
		}
		//sort for in-order output
		Collections.sort(theObjs);
		for(TreeObject to : theObjs) {
			dumpWriter.printf("%s: %d\n", kc.decodeKey(to.objKey, ArgsCreate.sequenceLength), to.objFreq);
		}

		dumpWriter.close();
		System.err.println("dump was successfully written.");
	}
}//no mass
