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
        getLog().info("--------------------------------------------------------");
        getLog().info("(a) Executing javamop -merge " + pathToMopFiles + "/*.mop");
        getLog().info("--------------------------------------------------------");

        ProcessUtil.executeExternalProgram(getLog(),
                pathToJavaMop + "/javamop"
                , "-merge"
                , pathToMopFiles + "/*.mop");
    }

    private void executeRVMonitor() throws IOException {

        getLog().info("--------------------------------------------------------");
        getLog().info("(b) Executing rv-monitor -merge " + pathToMopFiles + "/*.rvm");
        getLog().info("--------------------------------------------------------");
        ProcessUtil.executeExternalProgram(getLog(),
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
