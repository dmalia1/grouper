/*
 * Copyright (C) 2006 blair christensen.
 * All Rights Reserved.
 *
 * You may use and distribute under the same terms as Grouper itself.
 */

package com.devclue.grouper.shell;
import  bsh.*;
import  java.util.*;

/**
 * Delete a stem.
 * <p/>
 * @author  blair christensen.
 * @version $Id: delStem.java,v 1.3 2006-06-21 20:28:55 blair Exp $
 * @since   0.0.1
 */
public class delStem {

  // PUBLIC CLASS METHODS //

  /**
   * Delete a stem.
   * <p/>
   * @param   i     BeanShell interpreter.
   * @param   stack BeanShell call stack.
   * @param   name  <i>name</i> of {@link Stem} to delete.
   * @since   0.0.1
   */
  public static void invoke(Interpreter i, CallStack stack, String name) {
    StemHelper.delStem(i, name);
  } // public static void invoke(i, stack, parent, name)

} // public class delStem

