/*
 * Copyright (C) 2004-2005 University Corporation for Advanced Internet Development, Inc.
 * Copyright (C) 2004-2005 The University Of Chicago
 * All Rights Reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *  * Neither the name of the University of Chicago nor the names
 *    of its contributors nor the University Corporation for Advanced
 *   Internet Development, Inc. may be used to endorse or promote
 *   products derived from this software without explicit prior
 *   written permission.
 *
 * You are under no obligation whatsoever to provide any enhancements
 * to the University of Chicago, its contributors, or the University
 * Corporation for Advanced Internet Development, Inc.  If you choose
 * to provide your enhancements, or if you choose to otherwise publish
 * or distribute your enhancements, in source code form without
 * contemporaneously requiring end users to enter into a separate
 * written license agreement for such enhancements, then you thereby
 * grant the University of Chicago, its contributors, and the University
 * Corporation for Advanced Internet Development, Inc. a non-exclusive,
 * royalty-free, perpetual license to install, use, modify, prepare
 * derivative works, incorporate into the software or other computer
 * software, distribute, and sublicense your enhancements or derivative
 * works thereof, in binary and source code form.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND WITH ALL FAULTS.  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT ARE DISCLAIMED AND the
 * entire risk of satisfactory quality, performance, accuracy, and effort
 * is with LICENSEE. IN NO EVENT SHALL THE COPYRIGHT OWNER, CONTRIBUTORS,
 * OR THE UNIVERSITY CORPORATION FOR ADVANCED INTERNET DEVELOPMENT, INC.
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package test.edu.internet2.middleware.grouper;

import  edu.internet2.middleware.grouper.*;
import  edu.internet2.middleware.subject.*;

import  java.util.*;
import  junit.framework.*;


public class TestBug405 extends TestCase {

  private GrouperSession  s;
  private GrouperQuery    q;

  public TestBug405(String name) {
    super(name);
  }

  protected void setUp () {
    DB db = new DB();
    db.emptyTables();
    db.stop();
    s = Constants.createSession();
    Constants.createGroups(s);
    Constants.createMembers(s);
  }

  protected void tearDown () {
    s.stop();
  }


  /*
   * TESTS
   */

  public void testBug405MshipsThenPrivs() {
    // gA == "admins"
    // gB == "all"
    // gC == "finance"

    // Assert size of mships and privs
    Assert.assertTrue(
      "[0] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[0] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[0] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );

    // Create the mships and assert size of mships and privs
    try {
      Constants.gA.listAddVal(Constants.m0);
      Assert.assertTrue("add m0 to gA", true);
    } catch (Exception e) {
      Assert.fail("add m0 to gA");
    }
    try {
      Constants.gB.listAddVal(Constants.m0);
      Assert.assertTrue("add m0 to gB", true);
    } catch (Exception e) {
      Assert.fail("add m0 to gB");
    }
    try {
      Constants.gB.listAddVal(Constants.m1);
      Assert.assertTrue("add m1 to gB", true);
    } catch (Exception e) {
      Assert.fail("add m1 to gB");
    }
    Assert.assertTrue(
      "[1] gA members=1", Constants.gA.listVals().size() == 1
    );
    Assert.assertTrue(
      "[1] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[1] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[1] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[1] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );

    // Grant ADMIN and assert size of mships and privs
    Assert.assertTrue(
      "grant ADMIN to gA on gC",
      s.access().grant(
        s, Constants.gC, Constants.gA.toMember(), Grouper.PRIV_ADMIN
      )
    );
    Assert.assertTrue(
      "[2] gA members=1", Constants.gA.listVals().size() == 1
    );
    Assert.assertTrue(
      "[2] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[2] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[2] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[2] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[2] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[2] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );


    // Grant READ and assert size of mships and privs
    Assert.assertTrue(
      "grant READ to gB on gA",
      s.access().grant(
        s, Constants.gA, Constants.gB.toMember(), Grouper.PRIV_READ
      )
    );
    Assert.assertTrue(
      "[3] gA members=1", Constants.gA.listVals().size() == 1
    );
    Assert.assertTrue(
      "[3] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[3] gA READERS=3", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 3
    );
    Assert.assertTrue(
      "[3] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[3] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[3] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[3] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[3] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[3] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );

  }

  public void testBug405PrivsThenMshipPrivs() {
    // gA == "admins"
    // gB == "all"
    // gC == "finance"

    // Assert size of mships and privs
    Assert.assertTrue(
      "[0] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[0] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[0] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );

    // Grant READ and assert size of mships and privs
    Assert.assertTrue(
      "grant READ to gB on gA",
      s.access().grant(
        s, Constants.gA, Constants.gB.toMember(), Grouper.PRIV_READ
      )
    );
    Assert.assertTrue(
      "[1] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gA READERS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 1
    );
    Assert.assertTrue(
      "[1] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[1] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );

    // Grant ADMIN and assert size of mships and privs
    Assert.assertTrue(
      "grant ADMIN to gA on gC",
      s.access().grant(
        s, Constants.gC, Constants.gA.toMember(), Grouper.PRIV_ADMIN
      )
    );
    Assert.assertTrue(
      "[2] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "[2] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gA READERS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 1
    );
    Assert.assertTrue(
      "[2] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[2] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[2] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[2] gC ADMINS=2", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[2] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );

    // Create the mships and assert size of mships and privs
    try {
      Constants.gA.listAddVal(Constants.m0);
      Assert.assertTrue("add m0 to gA", true);
    } catch (Exception e) {
      Assert.fail("add m0 to gA");
    }
    try {
      Constants.gB.listAddVal(Constants.m0);
      Assert.assertTrue("add m0 to gB", true);
    } catch (Exception e) {
      Assert.fail("add m0 to gB");
    }
    try {
      Constants.gB.listAddVal(Constants.m1);
      Assert.assertTrue("add m1 to gB", true);
    } catch (Exception e) {
      Assert.fail("add m1 to gB");
    }
    Assert.assertTrue(
      "[3] gA members=1", Constants.gA.listVals().size() == 1
    );
    Assert.assertTrue(
      "[3] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[3] gA READERS=3", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 3
    );
    Assert.assertTrue(
      "[3] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[3] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[3] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[3] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[3] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[3] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );

  }

  public void testBug405AnotherOneForward() {
    //  gA  == students
    //  gB  == staff
    //  gC  == all
    //  gD  == admins

    //  m0  == iawi
    //  m1  == iati
    //  m2  == pepo
    //  m3  == pebe
    //  m4  == kebe

    // Assert size of mships and privs
    Assert.assertTrue(
      "[0] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[0] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[0] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[0] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gA:ADMIN grant to gD
    Assert.assertTrue(
      "gA:ADMIN grant to gD",
      s.access().grant(
        s, Constants.gA, Constants.gD.toMember(), Grouper.PRIV_ADMIN
      )
    );
    Assert.assertTrue(
      "[1] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[1] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[1] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[1] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[1] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gA add member m0
    try {
      Constants.gA.listAddVal(Constants.m0);
      Assert.assertTrue("gA add m0", true);
    } catch (Exception e) {
      Assert.fail("gA add m0: " + e.getMessage());
    }
    Assert.assertTrue(
      "[2] gA members=1", Constants.gA.listVals().size() == 1
    );
    Assert.assertTrue(
      "[2] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[2] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[2] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[2] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[2] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[2] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[2] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[2] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gA add member m1
    try {
      Constants.gA.listAddVal(Constants.m1);
      Assert.assertTrue("gA add m1", true);
    } catch (Exception e) {
      Assert.fail("gA add m1: " + e.getMessage());
    }
    Assert.assertTrue(
      "[3] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[3] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[3] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[3] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[3] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[3] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[3] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[3] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[3] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[3] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[3] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[3] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gB:ADMIN grant to gD
    Assert.assertTrue(
      "gB:ADMIN grant to gD",
      s.access().grant(
        s, Constants.gB, Constants.gD.toMember(), Grouper.PRIV_ADMIN
      )
    );
    Assert.assertTrue(
      "[4] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[4] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[4] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[4] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[4] gB ADMINS=2", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[4] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[4] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[4] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[4] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[4] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[4] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[4] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gB:ADMIN grant to m3
    Assert.assertTrue(
      "gB:ADMIN grant to m3",
      s.access().grant(
        s, Constants.gB, Constants.m3, Grouper.PRIV_ADMIN
      )
    );
    Assert.assertTrue(
      "[5] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[5] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[5] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[5] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[5] gB ADMINS=3", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[5] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[5] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[5] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[5] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[5] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[5] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[5] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gB add member m2
    try {
      Constants.gB.listAddVal(Constants.m2);
      Assert.assertTrue("gB add m2", true);
    } catch (Exception e) {
      Assert.fail("gB add m2: " + e.getMessage());
    }
    Assert.assertTrue(
      "[6] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[6] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[6] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[6] gB members=1", Constants.gB.listVals().size() == 1
    );
    Assert.assertTrue(
      "[6] gB ADMINS=3", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[6] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[6] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[6] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[6] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[6] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[6] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[6] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gB add member m3
    try {
      Constants.gB.listAddVal(Constants.m3);
      Assert.assertTrue("gB add m3", true);
    } catch (Exception e) {
      Assert.fail("gB add m3: " + e.getMessage());
    }
    Assert.assertTrue(
      "[7] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[7] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[7] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[7] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[7] gB ADMINS=3", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[7] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[7] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[7] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[7] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[7] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[7] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[7] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gC:ADMIN grant to gD
    Assert.assertTrue(
      "gC:ADMIN grant to gD",
      s.access().grant(
        s, Constants.gC, Constants.gD.toMember(), Grouper.PRIV_ADMIN
      )
    );
    Assert.assertTrue(
      "[8] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[8] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[8] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[8] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[8] gB ADMINS=3", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[8] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[8] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[8] gC ADMINS=2", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[8] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[8] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[8] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[8] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gC add member gA
    try {
      Constants.gC.listAddVal(Constants.gA.toMember());
      Assert.assertTrue("gC add gA", true);
    } catch (Exception e) {
      Assert.fail("gC add gA: " + e.getMessage());
    }
    Assert.assertTrue(
      "[9] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[9] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[9] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[9] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[9] gB ADMINS=3", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[9] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[9] gC members=3", Constants.gC.listVals().size() == 3
    );
    Assert.assertTrue(
      "[9] gC ADMINS=2", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[9] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[9] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[9] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[9] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gC add member gB
    try {
      Constants.gC.listAddVal(Constants.gB.toMember());
      Assert.assertTrue("gC add gB", true);
    } catch (Exception e) {
      Assert.fail("gC add gB: " + e.getMessage());
    }
    Assert.assertTrue(
      "[10] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[10] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[10] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[10] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[10] gB ADMINS=3", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[10] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[10] gC members=6", Constants.gC.listVals().size() == 6
    );
    Assert.assertTrue(
      "[10] gC ADMINS=2", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[10] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[10] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[10] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[10] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gD:ADMIN grant to gD
    Assert.assertTrue(
      "gD:ADMIN grant to gD",
      s.access().grant(
        s, Constants.gD, Constants.gD.toMember(), Grouper.PRIV_ADMIN
      )
    );
    Assert.assertTrue(
      "[11] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[11] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[11] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[11] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[11] gB ADMINS=3", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[11] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[11] gC members=6", Constants.gC.listVals().size() == 6
    );
    Assert.assertTrue(
      "[11] gC ADMINS=2", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[11] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[11] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[11] gD ADMINS=2", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[11] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gD:READ grant to gC
    Assert.assertTrue(
      "gD:READ grant to gC",
      s.access().grant(
        s, Constants.gD, Constants.gC.toMember(), Grouper.PRIV_READ
      )
    );
    Assert.assertTrue(
      "[12] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[12] gA ADMINS=2", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[12] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[12] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[12] gB ADMINS=3", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[12] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[12] gC members=6", Constants.gC.listVals().size() == 6
    );
    Assert.assertTrue(
      "[12] gC ADMINS=2", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[12] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[12] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[12] gD ADMINS=2", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "[12] gD READERS=7",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 7
    );

    // gD add member m4
    try {
      Constants.gD.listAddVal(Constants.m4);
      Assert.assertTrue("gD add m4", true);
    } catch (Exception e) {
      Assert.fail("gD add m4: " + e.getMessage());
    }
    Assert.assertTrue(
      "[13] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "[13] gA ADMINS=3", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[13] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[13] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "[13] gB ADMINS=4", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 4
    );
    Assert.assertTrue(
      "[13] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[13] gC members=6", Constants.gC.listVals().size() == 6
    );
    Assert.assertTrue(
      "[13] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[13] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[13] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "[13] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "[13] gD READERS=7",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 7
    );
  }

  public void testBug405AnotherOneReverse() {
    //  gA  == students
    //  gB  == staff
    //  gC  == all
    //  gD  == admins

    //  m0  == iawi
    //  m1  == iati
    //  m2  == pepo
    //  m3  == pebe
    //  m4  == kebe

    // Assert size of mships and privs
    Assert.assertTrue(
      "[0] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[0] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[0] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[0] gD members=0", Constants.gD.listVals().size() == 0
    );
    Assert.assertTrue(
      "[0] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[0] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gD add member m4
    try {
      Constants.gD.listAddVal(Constants.m4);
      Assert.assertTrue("gD add m4", true);
    } catch (Exception e) {
      Assert.fail("gD add m4: " + e.getMessage());
    }
    Assert.assertTrue(
      "[1] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[1] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[1] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[1] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[1] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "[1] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[1] gD READERS=0",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 0
    );

    // gD:READ grant to gC
    Assert.assertTrue(
      "gD:READ grant to gC",
      s.access().grant(
        s, Constants.gD, Constants.gC.toMember(), Grouper.PRIV_READ
      )
    );
    Assert.assertTrue(
      "[2] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "[2] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[2] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "[2] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[2] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "[2] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "[2] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "[2] gD ADMINS=1", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "[2] gD READERS=1",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 1
    );

    // gD:ADMIN grant to gD
    Assert.assertTrue(
      "gD:ADMIN grant to gD",
      s.access().grant(
        s, Constants.gD, Constants.gD.toMember(), Grouper.PRIV_ADMIN
      )
    );
    int idx = 3;
    Assert.assertTrue(
      "["+idx+"] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=0", Constants.gC.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=1",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 1
    );

    // gC add gB
    try {
      Constants.gC.listAddVal(Constants.gB.toMember());
      Assert.assertTrue("gC add gB", true);
    } catch (Exception e) {
      Assert.fail("gC add gB: " + e.getMessage());
    }
    idx++;
    Assert.assertTrue(
      "["+idx+"] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=1", Constants.gC.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=2",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 2
    );

    // gC add gA
    try {
      Constants.gC.listAddVal(Constants.gA.toMember());
      Assert.assertTrue("gC add gA", true);
    } catch (Exception e) {
      Assert.fail("gC add gA: " + e.getMessage());
    }
    idx++;
    Assert.assertTrue(
      "["+idx+"] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=2", Constants.gC.listVals().size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=1", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=3",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 3
    );

    // gC:ADMIN grant to gD
    Assert.assertTrue(
      "gC:ADMIN grant to gD",
      s.access().grant(
        s, Constants.gC, Constants.gD.toMember(), Grouper.PRIV_ADMIN
      )
    );
    idx++;
    Assert.assertTrue(
      "["+idx+"] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=0", Constants.gB.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=2", Constants.gC.listVals().size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=3",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 3
    );

    // gB add m3
    try {
      Constants.gB.listAddVal(Constants.m3);
      Assert.assertTrue("gB add m3", true);
    } catch (Exception e) {
      Assert.fail("gB add m3: " + e.getMessage());
    }
    idx++;
    Assert.assertTrue(
      "["+idx+"] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=1", Constants.gB.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=3", Constants.gC.listVals().size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=4",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 4
    );

    // gB add m2
    try {
      Constants.gB.listAddVal(Constants.m2);
      Assert.assertTrue("gB add m2", true);
    } catch (Exception e) {
      Assert.fail("gB add m2: " + e.getMessage());
    }
    idx++;
    Assert.assertTrue(
      "["+idx+"] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=1", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=4", Constants.gC.listVals().size() == 4
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=5",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 5
    );

    // gB:ADMIN grant to m3
    Assert.assertTrue(
      "gB:ADMIN grant to m3",
      s.access().grant(
        s, Constants.gB, Constants.m3, Grouper.PRIV_ADMIN
      )
    );
    idx++;
    Assert.assertTrue(
      "["+idx+"] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=2", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=4", Constants.gC.listVals().size() == 4
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=5",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 5
    );

    // gB:ADMIN grant to gD
    Assert.assertTrue(
      "gB:ADMIN grant to gD",
      s.access().grant(
        s, Constants.gB, Constants.gD.toMember(), Grouper.PRIV_ADMIN
      )
    );
    idx++;
    Assert.assertTrue(
      "["+idx+"] gA members=0", Constants.gA.listVals().size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=4", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 4
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=4", Constants.gC.listVals().size() == 4
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=5",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 5
    );

    // gA add m1
    try {
      Constants.gA.listAddVal(Constants.m1);
      Assert.assertTrue("gA add m1", true);
    } catch (Exception e) {
      Assert.fail("gA add m1: " + e.getMessage());
    }
    idx++;
    Assert.assertTrue(
      "["+idx+"] gA members=1", Constants.gA.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=4", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 4
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=5", Constants.gC.listVals().size() == 5
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=6",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 6
    );

    // gA add m0
    try {
      Constants.gA.listAddVal(Constants.m0);
      Assert.assertTrue("gA add m0", true);
    } catch (Exception e) {
      Assert.fail("gA add m0: " + e.getMessage());
    }
    idx++;
    Assert.assertTrue(
      "["+idx+"] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=1", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=4", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 4
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=6", Constants.gC.listVals().size() == 6
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=7",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 7
    );

    // gA:ADMIN grant to gD
    Assert.assertTrue(
      "gA:ADMIN grant to gD",
      s.access().grant(
        s, Constants.gA, Constants.gD.toMember(), Grouper.PRIV_ADMIN
      )
    );
    idx++;
    Assert.assertTrue(
      "["+idx+"] gA members=2", Constants.gA.listVals().size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gA ADMINS=3", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gA READERS=0", 
      s.access().whoHas(s, Constants.gA, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gB members=2", Constants.gB.listVals().size() == 2
    );
    Assert.assertTrue(
      "["+idx+"] gB ADMINS=4", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_ADMIN).size() == 4
    );
    Assert.assertTrue(
      "["+idx+"] gB READERS=0", 
      s.access().whoHas(s, Constants.gB, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gC members=6", Constants.gC.listVals().size() == 6
    );
    Assert.assertTrue(
      "["+idx+"] gC ADMINS=3", 
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gC READERS=0",
      s.access().whoHas(s, Constants.gC, Grouper.PRIV_READ).size() == 0
    );
    Assert.assertTrue(
      "["+idx+"] gD members=1", Constants.gD.listVals().size() == 1
    );
    Assert.assertTrue(
      "["+idx+"] gD ADMINS=3", 
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_ADMIN).size() == 3
    );
    Assert.assertTrue(
      "["+idx+"] gD READERS=7",
      s.access().whoHas(s, Constants.gD, Grouper.PRIV_READ).size() == 7
    );

  }
}

