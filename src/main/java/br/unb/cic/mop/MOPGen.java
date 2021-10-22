package br.unb.cic.mop;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

@Mojo(name = "mop-gen", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MOPGen extends AbstractMojo {

    @Parameter(property = "path-to-java-mop")
    private String pathToJavaMop;

    @Parameter(property = "path-to-rv-monitor")
    private String pathToMonitor;

    @Parameter(property = "path-to-mop-files")
    private String pathToMopFiles;

    @Parameter(property = "destination-package")
    private String destinationPackage;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            executeJavaMop();
            executeRVMonitor();
        }
        catch(IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    private void executeJavaMop() throws IOException  {
        System.out.println("--------------------------------------------------------");
        System.out.println("(a) Executing javamop -merge " + pathToMopFiles + "/*.mop");
        System.out.println("--------------------------------------------------------");
        ProcessUtil.executeExternalProgram(
                pathToJavaMop + "/javamop"
                , "-merge"
                , pathToMopFiles + "/*.mop");
    }

    private void executeRVMonitor() throws IOException {
        System.out.println("--------------------------------------------------------");
        System.out.println("(b) Executing rv-monitor -merge " + pathToMopFiles + "/*.rvm");
        System.out.println("--------------------------------------------------------");
        ProcessUtil.executeExternalProgram(
                pathToMonitor + "/rv-monitor"
                , "-merge"
                , "-d"
                , "./src/main/java/mop"
                , pathToMopFiles + "/*.rvm");

    }

}
