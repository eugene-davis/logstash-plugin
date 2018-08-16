/*
 * The MIT License
 *
 * Copyright 2014 Rusty Gerard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenkins.plugins.logstash.persistence.builddata;

import hudson.model.Run;
import hudson.model.Executor;
import hudson.model.Node;
import hudson.model.Action;
import hudson.model.Result;

import hudson.tasks.test.AbstractTestResultAction;

import jenkins.plugins.logstash.LogstashConfiguration;

import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.util.Calendar;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Abstract POJO for mapping build info to JSON.
 *
 * @author Rusty Gerard
 * @since 1.0.0
 */

public abstract class AbstractBuildData {
    protected String id;
    protected String result;
    protected String projectName;
    protected String fullProjectName;
    protected String displayName;
    protected String fullDisplayName;
    protected String description;
    protected String url;
    protected String buildHost;
    protected String buildLabel;
    protected int buildNum;
    protected long buildDuration;
    protected transient String timestamp;
    protected transient Run<?, ?> build;
    protected String rootProjectName;
    protected String rootFullProjectName;
    protected String rootProjectDisplayName;
    protected int rootBuildNum;
    protected Map<String, String> buildVariables;
    protected Set<String> sensitiveBuildVariables;
    protected TestData testResults = null;

    protected AbstractBuildData(Run<?, ?> build, Date currentTime) {
        this.build = build;
        Executor executor = build.getExecutor();
        if (executor == null) {
            buildHost = "master";
            buildLabel = "master";
        } else {
            Node node = executor.getOwner().getNode();
            if (node == null) {
              buildHost = "master";
              buildLabel = "master";
            } else {
              buildHost = StringUtils.isBlank(node.getDisplayName()) ? "master" : node.getDisplayName();
              buildLabel = StringUtils.isBlank(node.getLabelString()) ? "master" : node.getLabelString();
            }
        }
    
        id = build.getId();
        projectName = build.getParent().getName();
        fullProjectName = build.getParent().getFullName();
        displayName = build.getDisplayName();
        fullDisplayName = build.getFullDisplayName();
        description = build.getDescription();
        url = build.getUrl();
        buildNum = build.getNumber();
        buildDuration = currentTime.getTime() - build.getStartTimeInMillis();
        timestamp = LogstashConfiguration.getInstance().getDateFormatter().format(build.getTimestamp().getTime());
        updateResult();
    }

    public void updateResult()
    {
      if (result == null && build.getResult() != null)
      {
        Result result = build.getResult();
        this.result = result == null ? null : result.toString();
      }
      Action testResultAction = build.getAction(AbstractTestResultAction.class);
      if (testResults == null && testResultAction != null) {
        testResults = new TestData(testResultAction);
      }
    }

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().create();
      return gson.toJson(this);
    }
  
    public JSONObject toJson() {
      String data = toString();
      return JSONObject.fromObject(data);
    }
  
    public String getId() {
      return id;
    }
  
    public void setId(String id) {
      this.id = id;
    }
  
    public String getResult() {
      return result;
    }
  
    public void setResult(Result result) {
      this.result = result.toString();
    }
  
    public String getProjectName() {
      return projectName;
    }
  
    public void setProjectName(String projectName) {
      this.projectName = projectName;
    }
  
    public String getFullProjectName() {
      return fullProjectName;
    }
  
    public void setFullProjectName(String fullProjectName) {
      this.fullProjectName = fullProjectName;
    }
  
    public String getDisplayName() {
      return displayName;
    }
  
    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }
  
    public String getFullDisplayName() {
      return fullDisplayName;
    }
  
    public void setFullDisplayName(String fullDisplayName) {
      this.fullDisplayName = fullDisplayName;
    }
  
    public String getDescription() {
      return description;
    }
  
    public void setDescription(String description) {
      this.description = description;
    }
  
    public String getUrl() {
      return url;
    }
  
    public void setUrl(String url) {
      this.url = url;
    }
  
    public String getBuildHost() {
      return buildHost;
    }
  
    public void setBuildHost(String buildHost) {
      this.buildHost = buildHost;
    }
  
    public String getBuildLabel() {
      return buildLabel;
    }
  
    public void setBuildLabel(String buildLabel) {
      this.buildLabel = buildLabel;
    }
  
    public int getBuildNum() {
      return buildNum;
    }
  
    public void setBuildNum(int buildNum) {
      this.buildNum = buildNum;
    }
  
    public long getBuildDuration() {
      return buildDuration;
    }
  
    public void setBuildDuration(long buildDuration) {
      this.buildDuration = buildDuration;
    }
  
    public String getTimestamp() {
      return timestamp;
    }
  
    public void setTimestamp(Calendar timestamp) {
      this.timestamp = LogstashConfiguration.getInstance().getDateFormatter().format(timestamp.getTime());
    }
  
    public String getRootProjectName() {
      return rootProjectName;
    }
  
    public void setRootProjectName(String rootProjectName) {
      this.rootProjectName = rootProjectName;
    }
  
    public String getRootFullProjectName() {
      return rootFullProjectName;
    }
  
    public void setRootFullProjectName(String rootFullProjectName) {
      this.rootFullProjectName = rootFullProjectName;
    }
  
    public String getRootProjectDisplayName() {
      return rootProjectDisplayName;
    }
  
    public void setRootProjectDisplayName(String rootProjectDisplayName) {
      this.rootProjectDisplayName = rootProjectDisplayName;
    }
  
    public int getRootBuildNum() {
      return rootBuildNum;
    }
  
    public void setRootBuildNum(int rootBuildNum) {
      this.rootBuildNum = rootBuildNum;
    }
  
    public Map<String, String> getBuildVariables() {
      return buildVariables;
    }
  
    public void setBuildVariables(Map<String, String> buildVariables) {
      this.buildVariables = buildVariables;
    }
  
    public Set<String> getSensitiveBuildVariables() {
      return sensitiveBuildVariables;
    }
  
    public void setSensitiveBuildVariables(Set<String> sensitiveBuildVariables) {
      this.sensitiveBuildVariables = sensitiveBuildVariables;
    }
  
    public TestData getTestResults() {
      return testResults;
    }
  
    public void setTestResults(TestData testResults) {
      this.testResults = testResults;
    }
}