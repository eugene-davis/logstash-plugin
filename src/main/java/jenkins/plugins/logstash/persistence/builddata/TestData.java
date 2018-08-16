package jenkins.plugins.logstash.persistence.builddata;

import hudson.model.Action;

import hudson.tasks.test.AbstractTestResultAction;
import hudson.tasks.test.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public final class TestData {
    private int totalCount, skipCount, failCount, passCount;
    private List<FailedTest> failedTestsWithErrorDetail;
    private List<String> failedTests;

    public static class FailedTest {
      private final String fullName, errorDetails;
      public FailedTest(String fullName, String errorDetails) {
        super();
        this.fullName = fullName;
        this.errorDetails = errorDetails;
      }

      public String getFullName()
      {
        return fullName;
      }

      public String getErrorDetails()
      {
        return errorDetails;
      }
    }

    public TestData(Action action) {
      AbstractTestResultAction<?> testResultAction = null;
      if (action instanceof AbstractTestResultAction) {
        testResultAction = (AbstractTestResultAction<?>) action;
      }

      if (testResultAction == null) {
        totalCount = skipCount = failCount = 0;
        failedTests = Collections.emptyList();
        failedTestsWithErrorDetail = Collections.emptyList();
        return;
      }

      totalCount = testResultAction.getTotalCount();
      skipCount = testResultAction.getSkipCount();
      failCount = testResultAction.getFailCount();
      passCount = totalCount - skipCount - failCount;

      failedTests = new ArrayList<String>();
      failedTestsWithErrorDetail = new ArrayList<FailedTest>();
      for (TestResult result : testResultAction.getFailedTests()) {
          failedTests.add(result.getFullName());
          failedTestsWithErrorDetail.add(new FailedTest(result.getFullName(),result.getErrorDetails()));
      }
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public int getSkipCount()
    {
        return skipCount;
    }

    public int getFailCount()
    {
        return failCount;
    }

    public int getPassCount()
    {
        return passCount;
    }

    public List<FailedTest> getFailedTestsWithErrorDetail()
    {
        return failedTestsWithErrorDetail;
    }

    public List<String> getFailedTests()
    {
        return failedTests;
    }
}