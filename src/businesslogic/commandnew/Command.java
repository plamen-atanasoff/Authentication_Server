package businesslogic.commandnew;

public interface Command {
    void execute();
    Command copy();

    abstract class CommandCreator {
        protected CommandCreator(CommandType type) {
            CommandFactory.getInstance().addCreator(type, this);
        }

        abstract Command create(String[] input);
    }
}


