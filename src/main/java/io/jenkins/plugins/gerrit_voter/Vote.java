package io.jenkins.plugins.gerrit_voter;

import static java.util.Optional.ofNullable;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.restapi.RestApiException;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.util.FormValidation;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/** @author Réda Housni Alaoui */
public class Vote extends AbstractDescribableImpl<Vote> {

  private final String label;
  private final Map<Result, Short> valueByResult;

  @DataBoundConstructor
  public Vote(
      String label,
      short buildSuccessValue,
      short buildFailedValue,
      short buildUnstableValue,
      short buildAbortedValue) {

    this.label = StringUtils.trim(label);
    valueByResult = new HashMap<>();
    valueByResult.put(Result.SUCCESS, buildSuccessValue);
    valueByResult.put(Result.FAILURE, buildFailedValue);
    valueByResult.put(Result.UNSTABLE, buildUnstableValue);
    valueByResult.put(Result.ABORTED, buildAbortedValue);
  }

  public boolean isValid() {
    return StringUtils.isNotBlank(label);
  }

  public void castTo(RevisionApi revision, Result buildResult, PrintStream logger)
      throws RestApiException {
    ReviewInput reviewInput = new ReviewInput();
    reviewInput.labels = new HashMap<>();

    short voteValue = ofNullable(valueByResult.get(buildResult)).orElse((short) 0);
    String signedValue;
    if (voteValue > 0) {
      signedValue = "+" + voteValue;
    } else if (voteValue < 0) {
      signedValue = "-" + voteValue;
    } else {
      signedValue = String.valueOf(voteValue);
    }
    logger.println("Casting Gerrit vote " + signedValue + " on label '" + label + "'");
    reviewInput.labels.put(label, voteValue);
    revision.review(reviewInput);
  }

  public String getLabel() {
    return label;
  }

  public int getBuildSuccessValue() {
    return valueByResult.get(Result.SUCCESS);
  }

  public int getBuildFailedValue() {
    return valueByResult.get(Result.FAILURE);
  }

  public int getBuildUnstableValue() {
    return valueByResult.get(Result.UNSTABLE);
  }

  public int getBuildAbortedValue() {
    return valueByResult.get(Result.ABORTED);
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<Vote> {

    public FormValidation doCheckLabel(@QueryParameter String value)
        throws IOException, ServletException {
      if (StringUtils.isBlank(value)) {
        return FormValidation.error("The label is mandatory");
      }
      return FormValidation.ok();
    }

    public String getDisplayName() {
      return "Gerrit Vote";
    }
  }
}
