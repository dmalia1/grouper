/*
  Copyright 2004-2005 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2005 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package test.edu.internet2.middleware.grouper;

import  edu.internet2.middleware.grouper.*;
import  edu.internet2.middleware.subject.*;
import  edu.internet2.middleware.subject.provider.*;
import  junit.framework.*;

/**
 * {@link Stem} helper methods for testing the Grouper API.
 * <p />
 * @author  blair christensen.
 * @version $Id: StemHelper.java,v 1.5 2005-12-01 03:12:24 blair Exp $
 */
public class StemHelper {

  // Protected Class Methods

  // Add and test a child group
  // @return  Created {@link Group}
  protected static Group addChildGroup(Stem ns, String extn, String displayExtn) {
    try {
      Group child = ns.addChildGroup(extn, displayExtn);
      Assert.assertNotNull("child !null", child);
      Assert.assertTrue("added child group", true);
      Assert.assertTrue(
        "child group instanceof Group", 
        child instanceof Group
      );
      Assert.assertNotNull("child uuid !null", child.getUuid());
      Assert.assertTrue("child has uuid", !child.getUuid().equals(""));
      Assert.assertTrue(
        "parent stem", child.getParentStem().equals(ns)
      );
      Assert.assertTrue(
        "group extension", child.getExtension().equals(extn)
      );
      Assert.assertTrue(
        "group name", child.getName().equals(ns.getName() + ":" + extn)
      );
      Assert.assertTrue(
        "group displayExtension", 
        child.getDisplayExtension().equals(displayExtn)
      );
      Assert.assertTrue(
        "group displayName", 
        child.getDisplayName().equals(ns.getDisplayName() + ":" + displayExtn)
      );
      return child;
    }
    catch (GroupAddException eGA) {
      Assert.fail("failed to add group: " + eGA.getMessage());
    }
    catch (StemNotFoundException eSNF) {
      Assert.fail("failed to find parent stem" + eSNF.getMessage());
    }
    throw new RuntimeException(Helper.ERROR);
  } // protected static Group addChildGroup(ns, extn, displayExtn)

  // Add and test a child stem
  // @return  Created {@link Stem}
  protected static Stem addChildStem(Stem ns, String extn, String displayExtn) {
    try {
      Stem child = ns.addChildStem(extn, displayExtn);
      Assert.assertNotNull("child !null", child);
      Assert.assertTrue("added child stem", true);
      Assert.assertTrue(
        "child stem instanceof Stem", 
        child instanceof Stem
      );
      Assert.assertNotNull("child uuid !null", child.getUuid());
      Assert.assertTrue("child has uuid", !child.getUuid().equals(""));
      Assert.assertTrue(
        "parent stem", child.getParentStem().equals(ns)
      );
      return child;
    }
    catch (StemAddException e) {
      Assert.fail("failed to add stem: " + e.getMessage());
    }
    catch (StemNotFoundException eSNF) {
      Assert.fail("failed to find parent stem" + eSNF.getMessage());
    }
    throw new RuntimeException(Helper.ERROR);
  } // protected static Stem addChildStem(ns, extn, displayExtn)

  // Get the root stem
  // @return  The root {@link Stem}
  protected static Stem findRootStem(GrouperSession s) {
    Stem root = StemFinder.findRootStem(s);
    Assert.assertNotNull("root !null", root);
    Assert.assertTrue("found root stem", true);
    Assert.assertTrue(
      "root stem instanceof Stem", 
      root instanceof Stem
    );
    Assert.assertNotNull("root uuid !null", root.getUuid());
    Assert.assertTrue("root has uuid",      !root.getUuid().equals(""));
    Assert.assertTrue(
      "root extn", root.getExtension().equals("")
    );
    Assert.assertTrue(
      "root displayExtn", root.getDisplayExtension().equals("")
    );
    Assert.assertTrue(
      "root name", root.getName().equals("")
    );
    Assert.assertTrue(
      "root displayName", root.getDisplayName().equals("")
    );
    return root;
  } // protected static Stem findRootStem(s)

}

