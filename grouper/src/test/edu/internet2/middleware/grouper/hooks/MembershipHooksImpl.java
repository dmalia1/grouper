/**
 * Copyright 2012 Internet2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * @author mchyzer
 * $Id: MembershipHooksImpl.java,v 1.9 2009-03-20 19:56:41 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.hooks;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.helper.SubjectTestHelper;
import edu.internet2.middleware.grouper.hooks.beans.HooksContext;
import edu.internet2.middleware.grouper.hooks.beans.HooksMembershipChangeBean;
import edu.internet2.middleware.grouper.hooks.logic.HookVeto;


/**
 * test implementation of group hooks for test
 */
public class MembershipHooksImpl extends MembershipHooks {

  /** most recent subject id added to group */
  static String mostRecentAddMemberSubjectId;

  /** most recent subject id added to group */
  static String mostRecentAddCommitMemberSubjectId;

  /** keep track of hook count so we know how often it fires */
  static int preAddMemberHookCount = 0;

  /**
   * 
   * @see edu.internet2.middleware.grouper.hooks.MembershipHooks#membershipPreAddMember(edu.internet2.middleware.grouper.hooks.beans.HooksContext, edu.internet2.middleware.grouper.hooks.beans.HooksMembershipChangeBean)
   */
  @Override
  public void membershipPreAddMember(HooksContext hooksContext, 
      HooksMembershipChangeBean preAddMemberBean) {
    String subjectId = preAddMemberBean.getMember().getSubjectId();
    mostRecentAddMemberSubjectId = subjectId;
    preAddMemberHookCount++;
    if (StringUtils.equals(SubjectTestHelper.SUBJ1.getId(), subjectId)) {
      throw new HookVeto("hook.veto.subjectId.not.subj1", "subject cannot be subj1");
    }
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.hooks.MembershipHooks#membershipPostCommitAddMember(edu.internet2.middleware.grouper.hooks.beans.HooksContext, edu.internet2.middleware.grouper.hooks.beans.HooksMembershipChangeBean)
   */
  @Override
  public void membershipPostCommitAddMember(HooksContext hooksContext, 
      HooksMembershipChangeBean postAddCommitMemberBean) {
    String subjectId = postAddCommitMemberBean.getMember().getSubjectId();
    mostRecentAddCommitMemberSubjectId = subjectId;
  }
}
