package br.unb.cic.mop;

import org.apache.maven.plugin.MojoExecutionException;
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

    static void executeExternalProgram(Log log, String... args) throws MojoExecutionException, IOException {
        ProcessBuilder builder = new ProcessBuilder(args);
//        for(String k : environment.keySet()) {
//           System.out.println(environment.get(k));
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

        StringBuilder err = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line = null;
            while ((line = in.readLine()) != null) {
                err.append(line);
                err.append("\n");
            }
            if(err.length() > 0) {
                log.error(err);
                throw new MojoExecutionException(err.toString());
            }
        }

    }
}
