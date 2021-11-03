package br.unb.cic.mop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ProcessUtil {

    private static HashMap<String, String> environment = new HashMap<>();

    static void addVariable(String variable, String value) {
        environment.put(variable, value);
    }

    static void executeExternalProgram(String... args) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(args);

//        for(String k : environment.keySet()) {
//            builder.environment().put(k, environment.get(k));
//        }

        Process process = builder.start();

        StringBuilder out = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = null;
            while ((line = in.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
            System.out.println(out);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line = null;
            while ((line = in.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
            System.out.println(out);  // TODO: instead of writing in the standard
                                      //       output, we should perhaps write to a
                                      //       file (perhaps in an append mode).
                                      //       we should also check the exit value
                                      //       and perhaps kill the process.
                                      //       see: https://stackoverflow.com/questions/51520032/java-processbuilder-how-can-i-get-error-code-when-i-execute-an-incorrect-process
        }
    }
}
