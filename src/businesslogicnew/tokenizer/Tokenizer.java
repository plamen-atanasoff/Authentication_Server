package businesslogicnew.tokenizer;

import java.util.HashMap;
import java.util.Map;

public class Tokenizer {
    private static final String REGEX_TOKENS = "\\s+";
    private static final String REGEX_SOCKET_CHANNEL = "\\s+--";

    public static Map<String, String> tokenizeFromSocketChannel(String line) {
        Map<String, String> res = new HashMap<>();

        String[] tokens = line.split(REGEX_SOCKET_CHANNEL);

        for (String token : tokens) {
            String[] kvp = token.split(REGEX_TOKENS);
            if (kvp.length != 2) {
                throw new RuntimeException("Token is not in the right format");
            }

            res.put(kvp[0], kvp[1]);
        }

        return res;
    }
}
