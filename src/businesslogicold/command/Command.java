package businesslogicold.command;

import businesslogicold.tokenizer.Tokenizer;

import java.util.Map;

public record Command(String command, Map<String, String> arguments) {
    public static Command of(String line) {
        Map<String, String> tokens = Tokenizer.tokenizeFromSocketChannel(line);
        String command = tokens.get("command");
        tokens.remove("command");
        return new Command(command, tokens);
    }
}
