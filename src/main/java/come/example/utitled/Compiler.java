package come.example.utitled;

import come.example.utitled.system.Context;
import come.example.utitled.system.Emulator;
import come.example.utitled.system.CodeParser;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        var assemblerContext = new Context();
        new CodeParser(assemblerContext, "program_2.txt").parse();
        var emulator = new Emulator(assemblerContext);
        emulator.start();
    }
}
