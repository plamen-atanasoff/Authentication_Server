package businesslogic.tokenizer;

import java.util.HashMap;
import java.util.Map;

public class Tokenizer {
    private static final String REGEX_SOCKET_CHANNEL = "\\s+--";
    private static final String REGEX_DATABASE = ",(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))";

    public static Map<String, String> tokenizeFromSocketChannel(String line) {
        Map<String, String> map = new HashMap<>();

        line = line.strip();
        String[] tokens = line.split(REGEX_SOCKET_CHANNEL);

        map.put("command", tokens[0]);

        for (int i = 1; i < tokens.length; i++) {
            int end = tokens[i].indexOf(' ');
            String key = tokens[i].substring(0, end);
            String value = tokens[i].substring(end + 1);
            map.put(key, value);
        }

        return map;
    }

    public static String[] tokenizeFromDatabase(String line) {
        line = line.strip();
        String[] tokens = line.split(REGEX_DATABASE);

        tokens[1] = tokens[1].substring(1, tokens[1].length() - 1);

        return tokens;
    }
}
