/*
 * Copyright (C) 2006 blair christensen.
 * All Rights Reserved.
 *
 * You may use and distribute under the same terms as Grouper itself.
 */

package com.devclue.grouper.shell;
import  bsh.*;
import  java.io.*;

/**
 * {@link GrouperShell} Command Reader.
 * <p/>
 * @author  blair christensen.
 * @version $Id: CommandReader.java,v 1.1 2006-06-21 20:28:55 blair Exp $
 * @since   0.0.1
 */
class CommandReader {

  // PRIVATE INSTANCE VARIABLES //
  private int             cnt     = 0;
  private Interpreter     i       = null;
  private BufferedReader  in      = null;
  private String          prompt  = null;


  // CONSTRUCTORS //

  // @throws  GrouperShellException
  // @since   0.0.1
  protected CommandReader(String[] args) 
    throws  GrouperShellException
  {
    if (args.length > 0) {
      String file = args[0];
      if (file.equals("-")) {
        this.in = new BufferedReader( new InputStreamReader(System.in) );
      }
      else {
        try {
          this.in = new BufferedReader( new FileReader(file) );
        }
        catch (FileNotFoundException eFNF) {
          throw new GrouperShellException(eFNF);
        }
      }
    }
    else {
      this.in     = new BufferedReader( new InputStreamReader(System.in) );
      this.prompt = GrouperShell.NAME + "-" + GrouperShell.VERSION + " ";
    }
    this.i = new Interpreter(this.in, System.out, System.err, false);
  } // protected CommandReader(args)


  // PUBLIC INSTANCE METHODS //

  /**
   * @throws  GrouperShellException
   * @since   0.0.1
   */
  public Interpreter getInterpreter() 
    throws  GrouperShellException
  {
    if (this.i == null) {
      throw new GrouperShellException(E.I_NULL);
    }
    return this.i;
  } // public Interpreter getInterpreter()

  /**
   * @throws  GrouperShellException
   * @since   0.0.1
   */
  public String next() 
    throws  GrouperShellException
  {
    if (this.prompt != null) {
      this.i.print(this.prompt + this.cnt + "% ");
    }
    try {
      String cmd = this.in.readLine();
      try {
        GrouperShell.setHistory(this.i, cnt, cmd);
      }
      catch (bsh.EvalError eBEE) {
        this.i.error(E.GSH_SETHISTORY + eBEE.getMessage());
      }
      cnt++;
      return cmd;
    }
    catch (IOException eIO) {
      throw new GrouperShellException(eIO);
    }
  } // public String next()
 
} // class CommandReader

