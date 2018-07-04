
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParseGBK {
	private InputStreamReader inputStreamReader; // Sets up input stream reader
	private int origins = 0;
	private FileInputStream inputStreamFile;
	private HashMap<String, Integer> seqMap;// = new HashMap<String, Integer>();

	public ParseGBK() throws FileNotFoundException {
		
		inputStreamFile = new FileInputStream(ArgsCreate.fileName);
		inputStreamReader = new InputStreamReader(inputStreamFile);
		seqMap = new HashMap<String, Integer>();
	}

	// inputStreamFile.close();
	/**
	 * Reads the file, breaking each file into the separate ORIGIN sets of data.
	 * adds and returns all full sequences and their frequencies in an arrayList.
	 * 
	 * @throws IOException
	 */
	public ArrayList<TreeObject> Extract() throws IOException {

		ArrayList<TreeObject> retObjects = new ArrayList<>();
		boolean run = true; // Lets the program know the end of the Entire File has been reached
		while (run) {
			boolean endReached = false; // Lets the program know the end of the current ORIGIN has been reached
			origins++; // Counts how many ORIGINs program has ran through
			String sequence = ""; // DNA Sequence to be created into a BtreeNode
			int dataBitCheck; // Integer read in from inputStream, converts into character streamInput

			findOrigin(); // Finds the starting point (ORIGIN)
			while ((dataBitCheck = inputStreamReader.read()) != -1 && !endReached) {
				char newChar = (char) dataBitCheck;
				if (newChar == '/') { // If char is '/' we have reached the end of this ORIGIN subfile //Set sequence
										// to ""
					endReached = true; // Let program know so we can find next origin, or end of file
				} else if (newChar == 'n') { // Else if char is 'n'
					sequence = ""; // Reset the sequence
				} else if (newChar == 'a' || newChar == 'c' || newChar == 'g' || newChar == 't') { // Else if we have a
																									// character that
																									// should be added
																									// to string
					if (sequence.length() == ArgsCreate.sequenceLength) { // If DNA sequence is already a complete
																			// sequence...
						sequence = sequence.substring(1, sequence.length()) + "" + newChar; // Remove first two
																							// characters and add the
																							// last two to create a
																							// "sliding window" of data
					} else if (sequence.length() < (ArgsCreate.sequenceLength)) { // Else if sequence is incomplete...
						sequence += newChar; // Add the binary to the end of the string
					}
					if (sequence.length() == ArgsCreate.sequenceLength) { // If the sequence is the proper length 
						// use hash map to get frequency.
						if(seqMap.containsKey(sequence)) {
							seqMap.put(sequence, seqMap.get(sequence) + 1);
						}
						else {
							seqMap.put(sequence, 1);
						}
					}
				}
			}
			// When this origin is done running...
			if (inputStreamReader.read() == -1) { // If the very end of the file has been reached
				run = false; // Stop the program from reading
				System.err.println("End of File Reached");
			} 
		}
		// When all ORIGIN file sets have been read...
		inputStreamReader.close();
		// 
		for (Map.Entry<String, Integer> entry : seqMap.entrySet()) {
		String s = entry.getKey();// .toString();
		long key = toLong(s);
		int freq = entry.getValue();
		TreeObject treeObject = new TreeObject(key, freq);
						
		retObjects.add(treeObject);
	}
		return retObjects;
	}

	/**
	 * Finds the next ORIGIN if it exists
	 * 
	 * @throws IOException
	 */
	public void findOrigin() throws IOException {
		boolean originReached = false;
		char dataBit;
		int dataBitCheck;
		while ((dataBitCheck = inputStreamReader.read()) != -1 && !originReached) {
			dataBit = (char) dataBitCheck;
			if (dataBit == 'O') {
				if ((char) inputStreamReader.read() == 'R') {
					if ((char) inputStreamReader.read() == 'I') {
						if ((char) inputStreamReader.read() == 'G') {
							if ((char) inputStreamReader.read() == 'I') {
								if ((char) inputStreamReader.read() == 'N') {
									System.err.println("ORIGIN " + origins + " found. Reading.");
									originReached = true;
								}
							}
						}
					}
				}
			}
		}
	}
	public Long toLong(String str) {
		String binaryString = "";
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == 'a') {
				binaryString += "00";
			} else if (c == 'c') {
				binaryString += "01";
			} else if (c == 't') {
				binaryString += "11";
			} else if (c == 'g') {
				binaryString += "10";
			}
		}
		Long tempLong = Long.parseLong(binaryString, 2);
		return tempLong;
	}
}
