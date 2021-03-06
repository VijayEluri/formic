/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2010  Eric Van Dewoestine
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.formic.ant.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;

import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Replace;

import org.formic.ant.ClasspathResource;

/**
 * Utility methods for executing some common ant tasks.
 *
 * @author Eric Van Dewoestine
 */
public class AntUtils
{
  private static final String FORMIC_HOME_PROPERTY = "formic.home";
  private static final String FORMIC_HOME_ENV = "env.FORMIC_HOME";
  private static String formicHome;

  /**
   * Gets the location of the formic distribution based on the current
   * settings.
   *
   * @return The formic home directory.
   */
  public static String getFormicHome(Project project)
    throws BuildException
  {
    if(formicHome == null){
      // first try ant property.
      formicHome = project.getProperty(FORMIC_HOME_PROPERTY);

      // attempt to locate environment variable.
      if(formicHome == null){
        AntUtils.property(project, "env");
        formicHome = project.getProperty(FORMIC_HOME_ENV);
      }

      if(formicHome == null){
        throw new BuildException(
            "Unable to determine location of formic distribution:  " +
            "Property 'formic.home' not set. " +
            "Environment variable 'FORMIC_HOME' not found.");
      }
    }

    return formicHome;
  }
  /**
   * Executes a chmod for the specified file.
   *
   * @param project The current project.
   * @param file The file to chmod.
   * @param permissions The new file permissions.
   */
  public static void chmod(Project project, File file, String permissions)
  {
    Chmod chmod = new Chmod();
    chmod.setProject(project);
    chmod.setFile(file);
    chmod.setPerm(permissions);
    chmod.execute();
  }

  /**
   * Copies a file from src to dest.
   *
   * @param project The current project.
   * @param taskName The task name to use (name printed to console).
   * @param src The src file.
   * @param dest The dest file.
   */
  public static void copy(
      Project project, String taskName, File src, File dest)
  {
    Copy copy = new Copy();
    copy.setTaskName(taskName);
    copy.setProject(project);
    copy.setFile(src);
    copy.setTofile(dest);
    copy.execute();
  }

  /**
   * Make a new directory.
   *
   * @param project The current project.
   * @param taskName The task name to use (name printed to console).
   * @param dir The directory to create.
   */
  public static void mkdir(Project project, String taskName, File dir)
  {
    Mkdir mkdir = new Mkdir();
    mkdir.setProject(project);
    mkdir.setTaskName(taskName);
    mkdir.setDir(dir);
    mkdir.execute();
  }

  /**
   * Sets the environment property.
   *
   * @param project The current project.
   * @param env The environment property name to use.
   */
  public static void property(Project project, String env)
  {
    Property property = new Property();
    property.setProject(project);
    property.setEnvironment(env);
    property.execute();
  }

  /**
   * Sets a new property to the supplied value.
   *
   * @param project The current project.
   * @param name The property name.
   * @param value The property value.
   */
  public static void property(Project project, String name, String value)
  {
    Property property = new Property();
    property.setProject(project);
    property.setName(name);
    property.setValue(value);
    property.execute();
  }

  /**
   * Sets a new property to the value of the supplied location.
   *
   * @param project The current project.
   * @param name The property name.
   * @param location The location.
   */
  public static void property(Project project, String name, File location)
  {
    Property property = new Property();
    property.setProject(project);
    property.setName(name);
    property.setLocation(location);
    property.execute();
  }

  /**
   * Replace a token in the given file.
   *
   * @param project The current project.
   * @param file The file.
   * @param token The token to replace.
   * @param value The value to replace the token with.
   */
  public static void replace(
      Project project, File file, String token, String value)
  {
    Replace replace = new Replace();
    replace.setProject(project);
    replace.setFile(file);

    Replace.Replacefilter filter = replace.createReplacefilter();
    filter.setToken(token);
    filter.setValue(value);

    replace.execute();
  }

  /**
   * Resolves the specified file relative to the current basedir.
   *
   * @param project The current ant project.
   * @param file The file to resolve.
   * @return The resolved file.
   */
  public static File resolve(Project project, File file)
  {
    return new File(resolve(project, file.getAbsolutePath()));
  }

  /**
   * Resolves the specified file relative to the current basedir.
   *
   * @param project The current ant project.
   * @param file The file to resolve.
   * @return The resolved file.
   */
  public static String resolve(Project project, String file)
  {
    String basedir = project.getProperty("basedir");
    return FilenameUtils.concat(basedir, file);
  }

  /**
   * Configure the supplied project using the build file at the supplied
   * resource name location.
   *
   * @param resourceName The resource name.
   * @param project The project to configure.
   */
  public static void configureProjectFromResource(String resourceName, Project project)
  {
    ClasspathResource resource = new ClasspathResource(resourceName);

    //project.setUserProperty(MagicNames.ANT_FILE, resourceName);
    project.setUserProperty("ant.resource", resourceName);
    project.setUserProperty("ant.file.url", resource.getUrl().toString());
    project.setUserProperty(MagicNames.ANT_FILE_TYPE,
        MagicNames.ANT_FILE_TYPE_URL);

    ProjectHelper helper = ProjectHelperRepository.getInstance()
      .getProjectHelperForBuildFile(resource);
    project.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, helper);
    helper.parse(project, resource.getUrl());
  }
}
