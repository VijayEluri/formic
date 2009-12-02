/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008  Eric Van Dewoestine
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
package org.formic.ant.type;

import java.io.File;

import org.apache.tools.ant.Project;

import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.formic.ant.util.AntUtils;

/**
 * Extension to ant classpath element that represents the necessary jars for
 * formic.
 *
 * @author Eric Van Dewoestine
 */
public class Classpath
  extends Path
{
  public Classpath(Project project)
  {
    super(project);

    FileSet fileset = new FileSet();
    fileset.setDir(new File(AntUtils.getFormicHome(project)));
    fileset.setIncludes("ant/lib/*.jar");

    addFileset(fileset);
  }
}
