import java.io.File;

public class ArgsCreate {

	public static boolean useCache;
	public static int degree;
	public static String fileName;
	public static File geneBankFile;
	public static int sequenceLength;
	public static int cacheSize;
	public static int debugLevel;

	public static boolean validate(String[] args) {
		try {
			validateNumArgs(args.length);
			validateUseCache(args[0]);
			validateDegree(args[1]);
			validateGeneBankFile(args[2]);
			validateSequenceLength(args[3]);
			//no cache and debug option 1
			if (args.length == 5 && Integer.parseInt(args[0]) == 1) {
				validateCacheSize(args[4]);
			} else if (args.length == 5 && Integer.parseInt(args[0]) == 0) {
				validateDebugLevel(args[4]);
			}
			
			if (args.length == 6) {
				validateCacheSize(args[4]);
				validateDebugLevel(args[5]);
			}

		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			showUsage();
			return false;
		}

		return true;
	}

	private static void validateNumArgs(int num) {
		if (num < 4 || num > 6)
			throw new IllegalArgumentException("invalid number of arguments");
	}

	private static void validateUseCache(String s) {
		try {
			int i = Integer.parseInt(s);
			if (i == 0 || i == 1)
				useCache = i == 1;
			else
				throw new IllegalArgumentException(String.format("useCache argument '%s' must be either 0 or 1", s));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format("useCache argument '%s' must be an integer", s));
		}
	}

	private static void validateDegree(String s) {
			try {
				int deg = Integer.parseInt(s);

				if (deg == 1) {
					throw new IllegalArgumentException(
							String.format("degree argument '%s' degree can only be 0 or > 2", s));
				} else if (deg == 0) {
					int t = 0;
					int sum = 0;
					int diskSize = 4096;
					int sizeOfPointer = 8;
					int sizeOfTreeObject = 12;
					int sizeOfBTreeMetadata = 8;
					for (t = 0; sum <= diskSize - sizeOfBTreeMetadata; t++) {
						sum = (sizeOfTreeObject) * (2 * t - 1) + (sizeOfPointer) * (2 * t + 1);
					}
					degree = t - 1;
				} else {
					degree = deg;
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(String.format("degree argument '%s' not an integer", s));
			}
	}

	private static void validateGeneBankFile(String s) {
		fileName = s;
		File f = new File(s);

		if (f.exists()) {
			geneBankFile = f;
		} else {
			throw new IllegalArgumentException(String.format("geneBankFile file doesn't exist: %s", s));
		}

	}


	private static void validateSequenceLength(String s) {
		try {
			int i = Integer.parseInt(s);
			if (i > 0 && i <= 31)
				sequenceLength = i;
			else
				throw new IllegalArgumentException(
						String.format("sequenceLength argument '%s' must be larger than 1", s));

		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format("sequenceLength argument '%s' must be an integer", s));
		}
	}

	private static void validateCacheSize(String s) {
		try {
			int i = Integer.parseInt(s);
			if (i > 1)
				cacheSize = i;
			else
				throw new IllegalArgumentException(String.format("cacheSize argument '%s' must be larger than 1", s));

		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format("cacheSize argument '%s' must be an integer", s));
		}
	}

	private static void validateDebugLevel(String s) {
		try {
			int i = Integer.parseInt(s);
			if (i == 0 || i == 1)
				debugLevel = i;
			else
				throw new IllegalArgumentException(String.format("debugLevel argument '%s' must be either 0 or 1", s));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format("debugLevel argument '%s' must be an integer", s));
		}
	}

	/** prints the usage of the GeneBankSearch */
	private static void showUsage() {
		System.out.println("USAGE:");
		System.out.println(
				"java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
	}

}
