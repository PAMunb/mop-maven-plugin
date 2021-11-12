package br.unb.cic.mop;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

@Mojo(name = "agent-gen", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class AgentGen extends AbstractMojo  {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "path-to-java-mop")
    private String pathToJavaMop;

    @Parameter(property = "path-to-mop-files")
    private String pathToMopFiles;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            List<Dependency> cp = project.getDependencies();
            StringBuilder sb = new StringBuilder();

            for (Dependency d : cp) {
                sb.append(artifact(d));
                sb.append(":");
            }
            sb.append(project.getBasedir() + "/target/classes");

            System.out.println("--------------------------------------------------------");
            System.out.println("(c) Executing javamopagent " +  pathToMopFiles + "/*.aj");
            System.out.println("--------------------------------------------------------");

            ProcessUtil.addVariable("CLASSPATH", sb.toString());


            ProcessUtil.executeExternalProgram(pathToJavaMop + "/javamopagent",
                    pathToMopFiles + "/*.aj",
                    project.getBasedir() + "/target/classes", //"-v",
                    "-n",
                    "JavaMOPAgent");

        }catch(Exception e) {
            e.printStackTrace();
            throw new MojoExecutionException(e.getMessage());
        }
    }

    private String artifact(Dependency d) {
        return String.format("~/.m2/repository/%s/%s/%s/%s-%s.jar",
                d.getGroupId().replace(".", "/"),
                d.getArtifactId(),
                d.getVersion(),
                d.getArtifactId(),
                d.getVersion());
    }
}
