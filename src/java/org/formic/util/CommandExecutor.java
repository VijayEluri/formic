/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2006  Eric Van Dewoestine
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
package org.formic.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Thread to run the external command.
 */
public class CommandExecutor
  implements Runnable
{
  private int returnCode = -1;
  private String[] cmd;
  private String result;
  private String error;
  private Process process;

  /**
   * Construct a new instance.
   */
  private CommandExecutor (String[] cmd)
  {
    this.cmd = cmd;
  }

  /**
   * Execute the supplied command.
   *
   * @param cmd The command to execute.
   */
  public static CommandExecutor execute (String[] cmd)
    throws Exception
  {
    return execute(cmd, -1);
  }

  /**
   * Execute the supplied command.
   *
   * @param cmd The command to execute.
   * @param timeout Timeout in milliseconds.
   * @return The CommandExecutor instance containing the ending state of the
   * process.
   */
  public static CommandExecutor execute (String[] cmd, long timeout)
    throws Exception
  {
    CommandExecutor executor = new CommandExecutor(cmd);

    Thread thread = new Thread(executor);
    thread.start();

    if(timeout > 0){
      thread.join(timeout);
    }else{
      thread.join();
    }

    return executor;
  }

  /**
   * Run the thread.
   */
  public void run ()
  {
    try{
      Runtime runtime = Runtime.getRuntime();
      process = runtime.exec(cmd);

      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final ByteArrayOutputStream err = new ByteArrayOutputStream();

      Thread outThread = new Thread(){
        public void run (){
          try{
            copy(process.getInputStream(), out);
          }catch(IOException ioe){
            ioe.printStackTrace();
          }
        }
      };
      outThread.start();

      Thread errThread = new Thread(){
        public void run (){
          try{
            copy(process.getErrorStream(), err);
          }catch(IOException ioe){
            ioe.printStackTrace();
          }
        }
      };
      errThread.start();

      returnCode = process.waitFor();
      outThread.join(1000);
      errThread.join(1000);

      result = out.toString();
      error = err.toString();
    }catch(Exception e){
      returnCode = 12;
      error = e.getMessage();
      e.printStackTrace();
    }
  }

  /**
   * Copy the contents of the InputStream to the OutputStream.
   *
   * @param in The InputStream to read from.
   * @param out The OutputStream to write to.
   */
  private void copy (InputStream in, OutputStream out)
    throws IOException
  {
    byte[] buffer = new byte[1024 * 4];
    int n = 0;
    while (-1 != (n = in.read(buffer))) {
      out.write(buffer, 0, n);
    }
  }

  /**
   * Destroy this process.
   */
  public void destroy ()
  {
    if(process != null){
      process.destroy();
    }
  }

  /**
   * Gets the output of the command.
   *
   * @return The command result.
   */
  public String getResult ()
  {
    return result;
  }

  /**
   * Get the return code from the process.
   *
   * @return The return code.
   */
  public int getReturnCode ()
  {
    return returnCode;
  }

  /**
   * Gets the error message from the command if there was one.
   *
   * @return The possibly empty error message.
   */
  public String getErrorMessage ()
  {
    return error;
  }
}