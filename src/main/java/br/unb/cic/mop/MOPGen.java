package br.unb.cic.mop;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FilenameFilter;
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
            removeGeneratedJavaFiles();
            removeGeneratedMonitorFiles();
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

    private void removeGeneratedMonitorFiles() {
        File dest = new File(pathToJavaMop + "/javamop");

        if(dest.exists() && dest.isDirectory()) {
            String[] files = dest.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".aj") || name.endsWith("rvm");
                }
            });
            for(String s:  files) {
                File file = new File(s);
                file.delete();
            }
        }
    }

    private void removeGeneratedJavaFiles() {
        System.out.println("--------------------------------------------------------");
        System.out.println("(b) Removing generated Java files");
        System.out.println("--------------------------------------------------------");

        File dest = new File("./src/main/java/mop");

        if(dest.exists() && dest.isDirectory()) {
            String[] files = dest.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".java");
                }
            });
            for(String s:  files) {
                File file = new File(s);
                file.delete();
            }
        }
    }

}
