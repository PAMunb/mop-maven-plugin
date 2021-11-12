package br.unb.cic.mop;

import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ProcessUtil {

    private static HashMap<String, String> environment = new HashMap<>();

    static void addVariable(String variable, String value) {
        environment.put(variable, value);
    }

    static void executeExternalProgram(Log log, String... args) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(args);
        //TODO: I was wondering that the following code could help us to fix some CLASSPATH issues.
//        for(String k : environment.keySet()) {
//           builder.environment().put(k, environment.get(k));
//        }

        Process process = builder.start();

        StringBuilder out = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = null;
            while ((line = in.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
            log.info(out);
        }

        if(process.exitValue() != 0) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line = null;
                while ((line = in.readLine()) != null) {
                    out.append(line);
                    out.append("\n");
                }
                log.error(out);  // TODO: instead of writing in the standard
                //       output, we should write `out` to a
                //       file (in an append mode?).
                //       we should also check the exit value
                //       and perhaps kill the process.
                //       see: https://stackoverflow.com/questions/51520032/java-processbuilder-how-can-i-get-error-code-when-i-execute-an-incorrect-process
            }
        }
    }
}
