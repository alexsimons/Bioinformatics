/**
 * this class encodes or decodes TreeObject keys.
 * 
 */
public class KeyCoder {
	private StringBuilder sb;
	/**
	 * constructor
	 */
	public KeyCoder() {
		sb = new StringBuilder();
	}
	/**
	 * @param s
	 *            sequence to encode
	 * @return long value for key
	 */
	public long encodeKey(String s) {
		String seq = s;
		long retKey = 0;
		sb.setLength(0);
		sb.append(seq);
		sb.reverse();
		String subSeq = sb.toString();

		try {
			for (int i = 1; i <= seq.length(); i++) {
				long num = encode(subSeq.substring(i - 1, i));
				retKey = retKey | (num << 2 * (i - 1)); // bitwise
			}
		} catch (Exception e) {
			System.err.println("Issue converting sequence to key");
		}
		return retKey;
	}
	/**
	 * @param val
	 *            key from TreeObject
	 * @param upTo
	 *            sequence length from args.
	 * @return
	 */
	public String decodeKey(long val, int upTo) {
		String retSeq = "";
		for (int i = 0; i < upTo; i++) {
			long bits = (val >> 2 * i) & (~(~0 << 2));
			String s = decode(bits);
			retSeq += s;
		}
		sb.setLength(0);
		sb.append(retSeq);
		sb.reverse();
		return sb.toString();
	}
	/**
	 * 
	 * @param s
	 *            chars from DNA sequence
	 * @return binary representation
	 */
	private long encode(String s) {
		switch (s) {
		case "a":
			return 0b00;
		case "t":
			return 0b11;
		case "c":
			return 0b01;
		case "g":
			return 0b10;
		default:
			System.err.println("encoding Error: " + s);
			return -666;
		}
	}
	/**
	 * @param baseFour
	 *            bits from decodeKey
	 * @return letter representation
	 */
	public String decode(long baseFour) {
		if (baseFour == 0b00) {
			return "a";
		} else if (baseFour == 0b11) {
			return "t";
		} else if (baseFour == 0b01) {
			return "c";
		} else if (baseFour == 0b10) {
			return "g";
		} else {
			System.err.println("decoding Error");
			return "error";
		}
	}
}// end KeyOpps