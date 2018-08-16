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

import hudson.model.Environment;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Logger;
import static java.util.logging.Level.WARNING;
import java.lang.invoke.MethodHandles;

/**
 * POJO for mapping freestyle build info to JSON.
 *
 * @author Rusty Gerard
 * @since 1.0.0
 */
public class FreestyleBuildData extends AbstractBuildData {

  // TODO: Pass through during construction
  // ISO 8601 date format
  private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

  public FreestyleBuildData(AbstractBuild<?, ?> build, Date currentTime, TaskListener listener) {
    super(build, currentTime);

    // build.getDuration() is always 0 in Notifiers
    rootProjectName = build.getRootBuild().getProject().getName();
    rootFullProjectName = build.getRootBuild().getProject().getFullName();
    rootProjectDisplayName = build.getRootBuild().getDisplayName();
    rootBuildNum = build.getRootBuild().getNumber();
    buildVariables = build.getBuildVariables();
    sensitiveBuildVariables = build.getSensitiveBuildVariables();

    // Get environment build variables and merge them into the buildVariables map
    Map<String, String> buildEnvVariables = new HashMap<String, String>();
    List<Environment> buildEnvironments = build.getEnvironments();
    if (buildEnvironments != null) {
      for (Environment env : buildEnvironments) {
        if (env == null) {
          continue;
        }

        env.buildEnvVars(buildEnvVariables);
        if (!buildEnvVariables.isEmpty()) {
          buildVariables.putAll(buildEnvVariables);
          buildEnvVariables.clear();
        }
      }
    }
    try {
      buildVariables.putAll(build.getEnvironment(listener));
    } catch (Exception e) {
      // no base build env vars to merge
      LOGGER.log(WARNING,"Unable update logstash buildVariables with EnvVars from " + build.getDisplayName(),e);
    }
    for (String key : sensitiveBuildVariables) {
      buildVariables.remove(key);
    }
  }
}