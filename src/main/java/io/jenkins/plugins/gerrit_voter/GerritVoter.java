package io.jenkins.plugins.gerrit_voter;

import static java.util.Optional.ofNullable;

import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritManagement;
import com.sonyericsson.hudson.plugins.gerrit.trigger.config.IGerritHudsonTriggerConfig;
import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger;
import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTriggerParameters;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApi;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Job;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/** @author Réda Housni Alaoui */
public class GerritVoter extends Recorder {

  private final List<Vote> votes;

  @DataBoundConstructor
  public GerritVoter(List<Vote> votes) {
    this.votes =
        ofNullable(votes)
            .map(Collections::unmodifiableList)
            .orElseGet(Collections::emptyList)
            .stream()
            .filter(Vote::isValid)
            .collect(Collectors.toList());
  }

  public List<Vote> getVotes() {
    return votes;
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {

    if (votes.isEmpty()) {
      listener.getLogger().println("No Gerrit vote to cast");
      return true;
    }

    try {
      castVotes(build, listener);
    } catch (RestApiException e) {
      throw new IllegalStateException(e);
    }
    return true;
  }

  private void castVotes(AbstractBuild<?, ?> build, BuildListener listener)
      throws IOException, InterruptedException, RestApiException {

    listener.getLogger().println("Casting Gerrit votes");

    EnvVars environment = build.getEnvironment(listener);

    RevisionApi revision =
        createGerritApi(build.getParent())
            .changes()
            .id(environment.get(GerritTriggerParameters.GERRIT_CHANGE_NUMBER.name()))
            .revision(environment.get(GerritTriggerParameters.GERRIT_PATCHSET_NUMBER.name()));

    Result buildResult = build.getResult();
    for (Vote vote : votes) {
      vote.castTo(revision, buildResult, listener.getLogger());
    }
  }

  private GerritRestApi createGerritApi(Job<?, ?> project) {
    GerritTrigger trigger = GerritTrigger.getTrigger(project);
    String gerritServerName = trigger.getServerName();

    IGerritHudsonTriggerConfig gerritConfig = GerritManagement.getConfig(gerritServerName);
    boolean useRestApi = gerritConfig.isUseRestApi();

    String gerritFrontEndUrl = gerritConfig.getGerritFrontEndUrl();
    String username = gerritConfig.getGerritHttpUserName();
    String password = gerritConfig.getGerritHttpPassword();

    return new GerritRestApiFactory()
        .create(new GerritAuthData.Basic(gerritFrontEndUrl, username, password, useRestApi));
  }

  @Symbol("gerritVoter")
  @Extension
  public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
      return true;
    }

    @Override
    public String getDisplayName() {
      return "Cast Gerrit votes";
    }
  }
}
