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
/**
 * 
 */
package edu.internet2.middleware.grouper.rules.beans;

import edu.internet2.middleware.grouper.Stem;


/**
 * @author mchyzer
 *
 */
public class RulesStemBean extends RulesBean {

  /**
   * 
   */
  public RulesStemBean() {
    
  }
  
  /**
   * @see RulesBean#hasStem()
   */
  @Override
  public boolean hasStem() {
    return true;
  }

  /**
   * 
   * @param stem1
   */
  public RulesStemBean(Stem stem1) {
    super();
    this.stem = stem1;
  }


  /** stem */
  private Stem stem;

  /**
   * stem
   * @return stem
   */
  @Override
  public Stem getStem() {
    return this.stem;
  }

  /**
   * stem
   * @param stem1
   */
  public void setStem(Stem stem1) {
    this.stem = stem1;
  }

  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    if (this.stem != null) {
      result.append("stem: ").append(this.stem.getName()).append(", ");
    }
    return result.toString();
  }
  
  
}
