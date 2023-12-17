package come.example.utitled.emulator.asm.structure;

import come.example.utitled.utils.Operations;

public class TransitionCommand extends Command {

    private String nameFunctionRef;

    public TransitionCommand(Operations operations, String nameFunctionRef, Integer number) {
        this.operator = operations;
        this.nameFunctionRef = nameFunctionRef;
        this.number = number;
    }

    @Override
    public String getMainValue() {
        return nameFunctionRef;
    }

    @Override
    public String getSubMainValue() {
       return null;
    }

    @Override
    public void setMainValue(String command) {
        this.nameFunctionRef = command;
    }

    @Override
    public void setSubMainValue(String command) {
        // TODO
    }
}
