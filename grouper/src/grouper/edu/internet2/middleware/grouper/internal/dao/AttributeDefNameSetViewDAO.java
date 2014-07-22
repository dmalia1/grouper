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
 * $Id: AttributeDefNameSetViewDAO.java,v 1.1 2009-07-03 21:15:13 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.internal.dao;

import java.util.Set;

import edu.internet2.middleware.grouper.attr.AttributeDefNameSetView;

/**
 * attribute def name set views, links up attributes with other attributes (probably for privs)
 */
public interface AttributeDefNameSetViewDAO extends GrouperDAO {
  
  /**
   * find all attribute def name set views by related attribute def names (generally this is for testing)
   * @param attributeDefNames
   * @return the attr def name set views
   */
  public Set<AttributeDefNameSetView> findByAttributeDefNameSetViews(Set<String> attributeDefNames);
  
}
