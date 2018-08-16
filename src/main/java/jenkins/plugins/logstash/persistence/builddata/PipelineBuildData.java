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

import hudson.model.TaskListener;
import hudson.model.Run;

import java.util.Date;
import java.io.IOException;

import java.util.HashMap;

import java.util.logging.Logger;
import static java.util.logging.Level.WARNING;
import java.lang.invoke.MethodHandles;

/**
 * POJO for mapping pipeline build info to JSON.
 *
 * @author Rusty Gerard
 * @since 1.0.0
 */
public class PipelineBuildData extends AbstractBuildData {

  // TODO: Pass through during construction
  // ISO 8601 date format
  private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

  // Pipeline project build
  public PipelineBuildData(Run<?, ?> build, Date currentTime, TaskListener listener) {
    super(build, currentTime);

    rootProjectName = projectName;
    rootFullProjectName = fullProjectName;
    rootProjectDisplayName = displayName;
    rootBuildNum = buildNum;

    try {
      // TODO: sensitive variables are not filtered, c.f. https://stackoverflow.com/questions/30916085
      buildVariables = build.getEnvironment(listener);
    } catch (IOException | InterruptedException e) {
      LOGGER.log(WARNING,"Unable to get environment for " + build.getDisplayName(),e);
      buildVariables = new HashMap<String, String>();
    }
  }
}