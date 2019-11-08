package be.griffinbabe.webauctionhouse.command;

public enum AvailableCommands {

    HELP("wahhelp"),
    RESETDB("wahresetdb");


    public String name;

    AvailableCommands(String commandName) {
        this.name = commandName;
    }

}
