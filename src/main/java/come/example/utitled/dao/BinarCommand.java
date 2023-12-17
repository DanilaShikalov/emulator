package come.example.utitled.dao;

import come.example.utitled.utils.Operations;

public class BinarCommand extends Command {

    private String value1;
    private String value2;

    public BinarCommand(Operations operations, String value1, String value2, Integer number) {
        this.operator = operations;
        this.value1 = value1;
        this.value2 = value2;
        this.number = number;
    }

    @Override
    public String getMainValue() {
        return value1;
    }

    @Override
    public String getSubMainValue() {
        return value2;
    }

    @Override
    public void setMainValue(String command) {
        this.value1 = command;
    }

    @Override
    public void setSubMainValue(String command) {
        this.value2 = command;
    }
}
