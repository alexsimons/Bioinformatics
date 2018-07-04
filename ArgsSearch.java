import java.io.File;

public class ArgsSearch {

    public static boolean useCache;
    public static int debugLevel = 0;
    public static int cacheSize = 500;
    public static File queryFile;
    public static File btreeFile;

    public static boolean validate(String[] args) {

        try {
            validateNumArgs(args.length);
            validateUseCache(args[0]);
            validateBtreeFile(args[1]);
            validateQueryFile(args[2]);

            if (args.length > 3 && useCache)
                validateCacheSize(args[3]);

            if (args.length > 4)
                validateDebugLevel(args[4]);

            if (debugLevel == 1 && !btreeFile.exists()) {
                throw new IllegalArgumentException(
                    "you must specify a btreeFile arg if debug is set to 1"
                );
            }


        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            showUsage();
            return false;
        }

        return true;
    }

    private static void validateNumArgs(int num) {
        if (num < 3 || num > 5)
            throw new IllegalArgumentException("invalid number of arguments");
    }

    private static void validateUseCache(String s) {
        try {
            int i = Integer.parseInt(s);
            if (i == 0 || i == 1)
                useCache = i == 1;
            else
                throw new IllegalArgumentException(
                    String.format("useCache argument '%s' must be either 0 or 1", s)
                );

        } catch(NumberFormatException e) {
            throw new IllegalArgumentException(
                String.format("useCache argument '%s' must be an integer", s)
            );
        }
    }

    private static void validateBtreeFile(String s) {
        btreeFile = new File(s);
    }

    private static void validateQueryFile(String s) {
        File f = new File(s);

        if (!f.exists())
            throw new IllegalArgumentException(
                String.format("invalid queryFile. path does not exit: %s", s)
            );
        else if (!f.isFile())
            throw new IllegalArgumentException(
                String.format("invalid queryFile. path is not a file: %s", s)
            );
        else if (!f.canRead())
            throw new IllegalArgumentException(
                String.format("invalid queryFile. cannot read the file: %s", s)
            );
        else
            queryFile = f;
    }

    private static void validateCacheSize(String s) {
        try {
            int i = Integer.parseInt(s);
            if (i > 1)
                cacheSize = i;
            else
                throw new IllegalArgumentException(
                    String.format("cacheSize argument '%s' must be larger than 1", s)
                );

        } catch(NumberFormatException e) {
            throw new IllegalArgumentException(
                String.format("cacheSize argument '%s' must be an integer", s)
            );
        }
    }

    private static void validateDebugLevel(String s) {
        try {
            int i = Integer.parseInt(s);
            if (i == 0 || i == 1)
                debugLevel = i;
            else
                throw new IllegalArgumentException(
                    String.format("debugLevel argument '%s' must be either 0 or 1", s)
                );
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException(
                String.format("debugLevel argument '%s' must be an integer", s)
            );
        }
    }

    /** prints the usage of the GeneBankSearch */
    private static void showUsage() {
        System.out.println("USAGE:");
        System.out.println("java GeneBankSearch <useCache> <btree file> <query file> [<cache size>] [<debug level>]");
    }

}
