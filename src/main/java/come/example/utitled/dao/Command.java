package come.example.utitled.dao;

import come.example.utitled.utils.Operations;

public abstract class Command {

    protected Operations operator;
    protected Integer number;

    public abstract String getMainValue();
    public abstract String getSubMainValue();
    public abstract void setMainValue(String command);
    public abstract void setSubMainValue(String command);

    public Operations getOperator() {
        return operator;
    }

    public String commandToString() {
        return String.format("%s %s %s", getOperator().getName(), getMainValue(), getSubMainValue());
    }

    public Integer getNumber() {
        return number;
    }
}
