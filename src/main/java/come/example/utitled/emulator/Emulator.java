package come.example.utitled.emulator;

import come.example.utitled.emulator.asm.structure.BinarCommand;
import come.example.utitled.emulator.asm.structure.Command;
import come.example.utitled.emulator.asm.structure.flag.ZeroFlag;
import come.example.utitled.emulator.asm.structure.register.NumericRegister;
import come.example.utitled.emulator.asm.structure.register.RefRegister;
import come.example.utitled.emulator.asm.structure.register.Register;
import come.example.utitled.emulator.asm.structure.register.RegisterName;
import come.example.utitled.utils.OperationsRealization;
import come.example.utitled.utils.Operations;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class Emulator {
    private static final List<String> supportedFullRegisters = List.of("eax", "esi", "ecx", "ebp", "esp", "edi");
    private static final List<String> supportedYangRegisters = List.of("ax", "dx", "esi");
    private AssemblerContext assemblerContext;
    private static Map<Operations, String> operationNumbers = new HashMap<>(6);
    private Map<RegisterName, Register> registers = new HashMap<>();
    private Iterator<Command> currentCommandIterator;
    private Iterator<Map.Entry<String, List<Command>>> functionCommandIterator;

    static {
        operationNumbers.put(Operations.MOV, "1");
        operationNumbers.put(Operations.XOR, "2");
        operationNumbers.put(Operations.ADD, "3");
        operationNumbers.put(Operations.IMUL, "4");
        operationNumbers.put(Operations.DEC, "5");
        operationNumbers.put(Operations.JNZ, "6");
    }

    public Emulator(AssemblerContext assemblerContext) {
        this.assemblerContext = assemblerContext;
        functionCommandIterator = assemblerContext.getFunctionCommandIterator();
        currentCommandIterator = functionCommandIterator.next().getValue().listIterator();
    }


    public void start() {
        Command command = doStep();
        while (command != null) {
            Map<RegisterName, Register> registerNameRegisterMap = processCommand(command);
            outRegistersData(registerNameRegisterMap, command, command.getNumber());
            command = doStep();
        }
    }

    public void outRegistersData(Map<RegisterName, Register> registers, Command command, int programCounter) {
        System.out.println(String.format("Команда на выполнение: %s.   PC: %d", command.commandToString(), programCounter));
        registers.entrySet()
                .forEach(register -> {
                    if (register.getValue() instanceof RefRegister ) {
                        System.out.println(String.format("%s  :  %s.", register.getKey().name(), AssemblerContext.getRefRegValue(((RefRegister)register.getValue()).getValue(), (RefRegister) register.getValue())));
                    } else {
                        System.out.println(String.format("%s  :  %s.", register.getKey().name(), register.getValue().getValue()));
                    }
                });

        System.out.println("-----------------------------------------------------");
    }

    public static String  binaryToDecimal(String binaryString) {
        int decimalValue = Integer.parseInt(binaryString, 2);
        return String.valueOf(decimalValue);
    }

    public Command doStep() {
        Command command;
        if (currentCommandIterator.hasNext()) {
            command = currentCommandIterator.next();
        } else {
            if (functionCommandIterator.hasNext()) {
                currentCommandIterator = functionCommandIterator.next().getValue().listIterator();
                command = currentCommandIterator.next();
            } else {
                return null;
            }
        }
        return command;
    }

    private Map<RegisterName, Register> processCommand(Command command) {
        Register firstRegister;
        if (command instanceof BinarCommand) {
            if (isRegister(command.getMainValue()) && isRegister(command.getSubMainValue())) {
                firstRegister = processNumericRegister(command.getMainValue(), 0);
                calculate(command.getOperator(), firstRegister, processNumericRegister(command.getSubMainValue(), 0));
            } else if (isRegister(command.getMainValue()) && isReference(command.getSubMainValue())) {
                RefRegister refRegister = processRefRegister(command.getSubMainValue());
                firstRegister = processNumericRegister(command.getMainValue(), 0);
                calculate(command.getOperator(), firstRegister, refRegister);
            } else if (isRegister(command.getMainValue()) && isValue(command.getSubMainValue())) {
                firstRegister = processNumericRegister(command.getMainValue(), 0);
                if (command.getSubMainValue().contains("[")) {
                    String argsName = StringUtils.replaceEach(command.getSubMainValue(), new String[]{"[","]"}, new String[]{"", ""});
                    if (isRegister(argsName)) {
                        calculate(command.getOperator(), firstRegister, registers.get(RegisterName.valueOf(StringUtils.toRootUpperCase(argsName))));
                    } else {
                        calculateWithValue(command.getOperator(), firstRegister, Integer.parseInt((String) assemblerContext.getDataByName(argsName).getValue()));
                    }
                } else {
                    calculateWithValue(command.getOperator(), firstRegister, Integer.valueOf(command.getSubMainValue()));
                }
            }
        } else {
            if (command.getOperator().equals(Operations.JNZ)) {
                if (!ZeroFlag.isEnd()) {
                    currentCommandIterator = assemblerContext.getCommands().get(command.getMainValue()).iterator();
                } else {
                    System.out.println("end");
                }
                return registers;
            }
            firstRegister = processNumericRegister(command.getMainValue(), 0);
            calculateUnarCommand(command.getOperator(), firstRegister);
        }
        return registers;

    }

    private void calculateUnarCommand(Operations operator, Register register) {
        if (Objects.requireNonNull(operator) == Operations.DEC) {
            register = OperationsRealization.DEC_VALUE.apply(register);
            if (register.getValue().equals(0)) {
                ZeroFlag.changeFlag();
            }
        }
    }

    private void calculateWithValue(Operations operator, Register firstRegister, Integer value) {
        switch (operator) {
            case MOV:
                OperationsRealization.MOVE_VALUE.apply(firstRegister, value);
                break;
            case ADD:
                if (firstRegister instanceof RefRegister) {
                    RefRegister ref = (RefRegister) firstRegister;
                    ref.iterate(value / 2);
                } else {
                    OperationsRealization.ADD_VALUE.apply(firstRegister, value);
                }
                break;
            default:
                break;
        }
    }

    private void calculate(Operations operator, Register firstRegister, Register secondRegister) {
        switch (operator) {
            case XOR:
                OperationsRealization.XOR_REGISTER.accept(firstRegister, secondRegister);
                break;
            case ADD:
                OperationsRealization.ADD_REGISTER.accept(firstRegister, secondRegister);
                break;
            case MOV:
                var register = secondRegister instanceof RefRegister ? OperationsRealization.MOV_REF_REGISTER.apply(firstRegister, secondRegister) :
                        OperationsRealization.MOV_REGISTER.apply(firstRegister, secondRegister);
                registers.replace(register.getRegisterName(), register);
                break;
            case IMUL:
                OperationsRealization.IMUL_REGISTER.accept(firstRegister, secondRegister);
                break;
            default:
                break;
        }
    }

    private static boolean isRegister(String name) {
        return supportedFullRegisters.contains(name) || supportedYangRegisters.contains(name);
    }

    private boolean isReference(String name) {
        return assemblerContext.hasArray(name);
    }

    private boolean isValue(String name) {
        return name.contains("[") || (!isReference(name) && !isRegister(name));
    }

    public Register processNumericRegister(String name, Integer value) {
        NumericRegister numericRegister = null;
        if (registers.containsKey(RegisterName.valueOf(StringUtils.toRootUpperCase(name)))) {
            return registers.get(RegisterName.valueOf(StringUtils.toRootUpperCase(name)));
        } else {
            if (supportedYangRegisters.contains(name) && !registers.containsKey(name)) {
                numericRegister = new NumericRegister(RegisterName.valueOf(StringUtils.toRootUpperCase(name)), null, value);
            } else if (supportedFullRegisters.contains(name) && !registers.containsKey(name)) {
                numericRegister = new NumericRegister(RegisterName.valueOf(StringUtils.toRootUpperCase(name)), value, null);
            }
            registers.put(RegisterName.valueOf(StringUtils.toRootUpperCase(name)), numericRegister);
        }
        return numericRegister;
    }

    public RefRegister processRefRegister(String name) {
        RefRegister refRegister;

        if (AssemblerContext.hasArray(name)) {
            ArrayReference arrayReference = AssemblerContext.getArrayReferenceByName(name);
            refRegister = new RefRegister(RegisterName.REF, arrayReference, null);
        } else {
            throw new RuntimeException(String.format("Массив не найден :%s", name));
        }

        return refRegister;
    }
}
