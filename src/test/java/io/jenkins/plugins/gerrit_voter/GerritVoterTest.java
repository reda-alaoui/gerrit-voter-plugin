package io.jenkins.plugins.gerrit_voter;

import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;

public class GerritVoterTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

//    @Test
//    public void testConfigRoundtrip() throws Exception {
//        FreeStyleProject project = jenkins.createFreeStyleProject();
//        project.getBuildersList().add(new GerritVoter(Collections.emptyList()));
//        project = jenkins.configRoundtrip(project);
//        jenkins.assertEqualDataBoundBeans(new GerritVoter(Collections.emptyList()), project.getBuildersList().get(0));
//    }
//
//    @Test
//    public void testConfigRoundtripFrench() throws Exception {
//        FreeStyleProject project = jenkins.createFreeStyleProject();
//        GerritVoter voter = new GerritVoter(Collections.emptyList());
//        project.getBuildersList().add(voter);
//        project = jenkins.configRoundtrip(project);
//
//        GerritVoter lhs = new GerritVoter(Collections.emptyList());
//        jenkins.assertEqualDataBoundBeans(lhs, project.getBuildersList().get(0));
//    }
//
//    @Test
//    public void testBuild() throws Exception {
//        FreeStyleProject project = jenkins.createFreeStyleProject();
//        GerritVoter builder = new GerritVoter(Collections.emptyList());
//        project.getBuildersList().add(builder);
//
//        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
//        jenkins.assertLogContains("Hello, " + name, build);
//    }
//
//    @Test
//    public void testBuildFrench() throws Exception {
//
//        FreeStyleProject project = jenkins.createFreeStyleProject();
//        GerritVoter voter = new GerritVoter(Collections.emptyList());
//        project.getBuildersList().add(voter);
//
//        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
//        jenkins.assertLogContains("Bonjour, " + name, build);
//    }
//
//    @Test
//    public void testScriptedPipeline() throws Exception {
//        String agentLabel = "my-agent";
//        jenkins.createOnlineSlave(Label.get(agentLabel));
//        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "test-scripted-pipeline");
//        String pipelineScript
//                = "node {\n"
//                + "  greet '" + name + "'\n"
//                + "}";
//        job.setDefinition(new CpsFlowDefinition(pipelineScript, true));
//        WorkflowRun completedBuild = jenkins.assertBuildStatusSuccess(job.scheduleBuild2(0));
//        String expectedString = "Hello, " + name + "!";
//        jenkins.assertLogContains(expectedString, completedBuild);
//    }

}
