/*
 * Copyright 2014 Thomas Bouffard (redfish4ktc)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.ktc.soapui.maven.extension;

import static org.ktc.soapui.maven.extension.impl.runner.SoapUITestCaseRunnerWrapper.newSoapUITestCaseRunnerWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;

public class TestMultiMojo extends TestMojo {

    private List<ProjectFilesScan> projectFiles;

    @Override
    protected void performRunnerExecute() throws MojoExecutionException, MojoFailureException {
        List<File> resolvedProjectFiles = resolveProjectFiles();
        for (File currentProjectFile : resolvedProjectFiles) {
            configureAndRun(newSoapUITestCaseRunnerWrapper(runnerType), currentProjectFile.getAbsolutePath());
        }
    }

    private List<File> resolveProjectFiles() {
        List<File> resolved = new ArrayList<File>();
        for (ProjectFilesScan scan : projectFiles) {
            DirectoryScanner scanner = new DirectoryScanner();
            File baseDirectory = scan.baseDirectory;
            // TODO add FollowSymlink?
            scanner.setBasedir(baseDirectory);
            // null check to keep includes default ("**")
            if (scan.includes != null) {
                scanner.setIncludes(toArray(scan.includes));
            }
            scanner.setExcludes(toArray(scan.excludes));
            scanner.scan();

            String[] includedFiles = scanner.getIncludedFiles();
            for (String includedFile : includedFiles) {
                resolved.add(new File(baseDirectory, includedFile));
            }
        }
        return resolved;
    }

    private static String[] toArray(Set<String> set) {
        return set == null ? null : set.toArray(new String[set.size()]);
    }

    public static class ProjectFilesScan {
        public File baseDirectory;
        public Set<String> includes;
        public Set<String> excludes;
    }

}
