package come.example.utitled.dao;

import come.example.utitled.utils.Operations;

public class UnarCommand extends Command {

    private final String value1;

    public UnarCommand(Operations operator, String value1, Integer number) {
        this.operator = operator;
        this.value1 = value1;
        this.number = number;
    }

    @Override
    public String getMainValue() {
        return value1;
    }

    @Override
    public String getSubMainValue() {
        return null;
    }

    @Override
    public void setMainValue(String command) {
        // TODO
    }

    @Override
    public void setSubMainValue(String command) {
        // TODO
    }
}
