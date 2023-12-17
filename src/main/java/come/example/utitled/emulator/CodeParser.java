package come.example.utitled.emulator;

import come.example.utitled.emulator.asm.structure.BinarCommand;
import come.example.utitled.emulator.asm.structure.Command;
import come.example.utitled.emulator.asm.structure.TransitionCommand;
import come.example.utitled.emulator.asm.structure.UnarCommand;
import come.example.utitled.syntax.*;
import come.example.utitled.utils.Operations;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static come.example.utitled.emulator.AssemblerSyntax.*;

public class CodeParser {
    private final AssemblerContext context;
    private static final AtomicInteger commandCounter = new AtomicInteger(0);
    private final Pattern arrayPattern = Pattern.compile("(\\w+)\\s+(\\w{2})\\s+(\\d+(?:,\\d+)+)");
    private final BufferedReader bufferedReader;

    public CodeParser(AssemblerContext context, String fileName) throws FileNotFoundException {
        this.context = context;
        this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.PROGRAM_PATH + fileName)));
    }

    private String readNext() throws IOException {
        String line;
        if ((line = bufferedReader.readLine()) != null) {
            while (line.isEmpty()) {
                line = bufferedReader.readLine();
            }
            return StringUtils.normalizeSpace(line);
        } else return null;
    }

    public void parse() throws IOException {
        var functionName = "";
        Command command = null;
        var line = readNext();
        if (line == null) {
            throw new RuntimeException("Line is null");
        }
        if (line.contains(SECTION) && line.contains(DOT_DATA)) {
            line = readNext();
            if (line == null) {
                throw new RuntimeException("Line is null");
            }
            while (!line.contains(DOT_TEXT)) {
                context.addData(readData(line));
                line = readNext();
                if (line == null) {
                    throw new RuntimeException("Line is null");
                }
            }
        }
        if (line.contains(SECTION) && line.contains(DOT_TEXT)) {
            line = readNext();
            assert line != null;
            if (line.contains(GLOBAL)) {
                functionName = line.split(" ")[1];
                line = readNext();
            }
            assert line != null;
            while ((line = readNext()) != null) {
                if (line.contains(":")) {
                    functionName = line.split(":")[0];
                } else if (line.contains(RET)) {
                    break;
                } else {
                    var subLine = line.split(" ");
                    if (line.contains(Operations.JNZ.getName())) {
                        command = new TransitionCommand(Operations.readOperation(subLine[0]), subLine[1], commandCounter.getAndIncrement());
                    } else if (subLine.length == 2) {
                        command = new UnarCommand(Operations.readOperation(subLine[0]), subLine[1], commandCounter.getAndIncrement());
                    } else if (subLine.length == 3) {
                        command = new BinarCommand(Operations.readOperation(subLine[0]), subLine[1], subLine[2], commandCounter.getAndIncrement());
                    }
                    assert command != null;
                    context.addCommands(functionName, filterLine(command));
                }
            }
        }
    }

    private Command filterLine(Command command) {
        command.setMainValue(StringUtils.replaceEach(command.getMainValue(), unnecessaryCharacters, voidCharacters));
        command.setSubMainValue(StringUtils.replaceEach(command.getSubMainValue(), unnecessaryCharacters, voidCharacters));
        return command;
    }

    public AsmData readData(String line) {
        var arrayMatcher = arrayPattern.matcher(line);
        var splitArray = line.split(" ");
        if (arrayMatcher.matches()) {
            return new AsmArray(readType(splitArray[1]), splitArray[0], List.of(splitArray[2].split(",")));
        } else {
            return new AsmNumber(readType(splitArray[1]), splitArray[0], splitArray[2]);
        }
    }

    private AssemblerType readType(String type) {
        return AssemblerType.readAsmType(type);
    }

}
