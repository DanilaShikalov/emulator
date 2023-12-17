package come.example.utitled.system;

import come.example.utitled.dao.BinarCommand;
import come.example.utitled.dao.Command;
import come.example.utitled.dao.EndFlag;
import come.example.utitled.dao.NumericRegister;
import come.example.utitled.dao.RefRegister;
import come.example.utitled.dao.Register;
import come.example.utitled.dao.RegisterName;
import come.example.utitled.utils.OperationsRealization;
import come.example.utitled.utils.Operations;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class Emulator {
    /**
     * Регистры по 4 байта
     */
    private static final List<String> fourByteRegisters = List.of("eax", "esi", "ecx", "ebp", "esp", "edi");
    /**
     * Регистры по 2 байта
     */
    private static final List<String> twoByteRegisters = List.of("ax", "dx");
    /**
     * Контекст
     */
    private final Context context;
    /**
     * Регистры
     */
    private final Map<RegisterName, Register> registers = new HashMap<>();
    /**
     * Итератор всех команд в контексте
     */
    private Iterator<Command> commandIterator;
    /**
     * Итератор по функциям
     */
    private final Iterator<Map.Entry<String, List<Command>>> functionIterator;

    public Emulator(Context context) {
        this.context = context;
        functionIterator = context.getFunctionCommandIterator();
        commandIterator = functionIterator.next().getValue().listIterator();
    }

    /**
     * Выполнение алгоритма функций
     */
    public void start() {
        var command = iterate();
        while (command != null) {
            var registerNameRegisterMap = compileCommand(command);
            printRegistersInfo(registerNameRegisterMap, command, command.getNumber());
            command = iterate();
        }
    }

    /**
     * Вывод информации по регистрам
     */
    public void printRegistersInfo(Map<RegisterName, Register> registers, Command command, int programCounter) {
        System.out.printf("Команда на выполнение: %s.   PC: %d%n", command.commandToString(), programCounter);
        registers.forEach((key, value) -> {
            if (value instanceof RefRegister) {
                System.out.printf("%s  :  %s.%n", key.name(), Context.getRefRegValue(((RefRegister) value).getValue(), (RefRegister) value));
            } else {
                System.out.printf("%s  :  %s.%n", key.name(), value.getValue());
            }
        });
        System.out.println("-----------------------------------------------------");
    }

    /**
     * Взять следующую команду в функции<p>
     * Если команды закончились, то перейти к следующей функции
     */
    public Command iterate() {
        Command command;
        if (commandIterator.hasNext()) {
            command = commandIterator.next();
        } else {
            if (functionIterator.hasNext()) {
                commandIterator = functionIterator.next().getValue().listIterator();
                command = commandIterator.next();
            } else {
                return null;
            }
        }
        return command;
    }

    /**
     * Выполнить команду<p>
     * Определяется кто из аргументов регистр и от этого поведение
     */
    private Map<RegisterName, Register> compileCommand(Command command) {
        Register firstRegister;
        if (command instanceof BinarCommand) {
            // Определение, что два аргумента это регистры
            if (isRegister(command.getMainValue()) && isRegister(command.getSubMainValue())) {
                firstRegister = processNumericRegister(command.getMainValue(), 0);
                calculate(command.getOperator(), firstRegister, processNumericRegister(command.getSubMainValue(), 0));
            // Определение, что первый аргумент это регистр, а второй это элемент массива (так как это адрес)
            } else if (isRegister(command.getMainValue()) && isArrayRef(command.getSubMainValue())) {
                var refRegister = processRefRegister(command.getSubMainValue());
                firstRegister = processNumericRegister(command.getMainValue(), 0);
                calculate(command.getOperator(), firstRegister, refRegister);
            // Определение, что первый аргумент это регистр, а второй в конце концов будет числом
            // Неважно, после нахождения по адресу или просто число
            } else if (isRegister(command.getMainValue()) && isValue(command.getSubMainValue())) {
                firstRegister = processNumericRegister(command.getMainValue(), 0);
                // Условие на то, что это может быть адрес [123] это значение по адресу 123
                if (command.getSubMainValue().contains("[")) {
                    String argsName = StringUtils.replaceEach(command.getSubMainValue(), new String[]{"[", "]"}, new String[]{"", ""});
                    if (isRegister(argsName)) {
                        // Посчитать как просто регистр и регистр
                        calculate(command.getOperator(), firstRegister, registers.get(RegisterName.valueOf(StringUtils.toRootUpperCase(argsName))));
                    } else {
                        // Посчитать как регистр и число
                        calculateWithValue(command.getOperator(), firstRegister, Integer.parseInt((String) Context.getDataByName(argsName).getValue()));
                    }
                } else {
                    // Иначе как просто регистр и самое обычное число
                    calculateWithValue(command.getOperator(), firstRegister, Integer.valueOf(command.getSubMainValue()));
                }
            }
        } else {
            // Определение конца функции
            if (command.getOperator().equals(Operations.JNZ)) {
                // Костыль
                if (!EndFlag.isEnd()) {
                    commandIterator = context.getCommands().get(command.getMainValue()).iterator();
                } else {
                    System.out.println("end");
                }
                return registers;
            }
            firstRegister = processNumericRegister(command.getMainValue(), 0);
            if (Objects.requireNonNull(command.getOperator()) == Operations.DEC) {
                firstRegister = OperationsRealization.DEC_VALUE.apply(firstRegister);
                if (firstRegister.getValue().equals(0)) {
                    EndFlag.changeFlag();
                }
            }
        }
        return registers;
    }

    /**
     * Выполнить операцию с числом
     */
    private void calculateWithValue(Operations operator, Register firstRegister, Integer value) {
        switch (operator) {
            case MOV:
                OperationsRealization.MOV_VALUE.apply(firstRegister, value);
                break;
            case ADD:
                if (firstRegister instanceof RefRegister) {
                    RefRegister ref = (RefRegister) firstRegister;
                    // Делим на 2, так как числа с 2 байтами
                    // А смещение по массиву на 2 байта равно одной итерации
                    ref.iterate(value / 2);
                } else {
                    OperationsRealization.ADD_VALUE.apply(firstRegister, value);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Выполнить операцию
     */
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
        return fourByteRegisters.contains(name) || twoByteRegisters.contains(name);
    }

    private boolean isArrayRef(String name) {
        return Context.hasArray(name);
    }

    private boolean isValue(String name) {
        return name.contains("[") || (!isArrayRef(name) && !isRegister(name));
    }

    /**
     * Ввод значения регистра в контекст
     * Если регистра нет, то завести, иначе просто присвоить новое значение
     */
    public Register processNumericRegister(String name, Integer value) {
        NumericRegister numericRegister = null;
        if (registers.containsKey(RegisterName.valueOf(StringUtils.toRootUpperCase(name)))) {
            return registers.get(RegisterName.valueOf(StringUtils.toRootUpperCase(name)));
        } else {
            if (twoByteRegisters.contains(name) && !registers.containsKey(name)) {
                numericRegister = new NumericRegister(RegisterName.valueOf(StringUtils.toRootUpperCase(name)), null, value);
            } else if (fourByteRegisters.contains(name) && !registers.containsKey(name)) {
                numericRegister = new NumericRegister(RegisterName.valueOf(StringUtils.toRootUpperCase(name)), value, null);
            }
            registers.put(RegisterName.valueOf(StringUtils.toRootUpperCase(name)), numericRegister);
        }
        return numericRegister;
    }

    /**
     * Получить массив чисел по названию
     */
    public RefRegister processRefRegister(String name) {
        RefRegister refRegister = null;
        if (Context.hasArray(name)) {
            var arrayReference = Context.getArrayReferenceByName(name);
            refRegister = new RefRegister(RegisterName.REF, arrayReference, null);
        }
        return refRegister;
    }
}
