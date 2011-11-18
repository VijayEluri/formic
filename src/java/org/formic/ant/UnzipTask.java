/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2011  Eric Van Dewoestine
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
package org.formic.ant;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.taskdefs.Expand;

import org.formic.util.Extractor;

/**
 * Ant task which unzips a zip resource from the installer jar.
 *
 * @author Eric Van Dewoestine
 */
public class UnzipTask
  extends Expand
{
  private String resource;

  /**
   * Executes this task.
   */
  public void execute()
    throws BuildException
  {
    File archive = null;
    try{
      archive = File.createTempFile(
          FilenameUtils.getBaseName(resource),
          "." + FilenameUtils.getExtension(resource));
      Extractor.readArchive(resource, archive);
      setSrc(archive);
      super.execute();
    }catch(IOException ioe){
      throw new BuildException(ioe);
    }finally{
      if (archive != null){
        // delete temp archive.
        archive.delete();
      }
    }
  }

  /**
   * Sets the resource to unzip.
   *
   * @param resource The resource.
   */
  public void setResource(String resource)
  {
    this.resource = resource;
  }
}
