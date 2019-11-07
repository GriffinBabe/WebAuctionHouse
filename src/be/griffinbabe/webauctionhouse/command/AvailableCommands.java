package be.griffinbabe.webauctionhouse.command;

public enum AvailableCommands {

    HELP("wah help"),
    RESETDB("wah reset-db");


    public String name;

    AvailableCommands(String commandName) {
        this.name = commandName;
    }

}
