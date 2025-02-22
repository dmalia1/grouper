/**
 * Copyright 2014 Internet2
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
 * $Id: GroupAttributeNameValidationHook.java,v 1.6 2009-03-24 17:12:08 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.hooks.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.attr.AttributeDefName;
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.hooks.AttributeDefNameHooks;
import edu.internet2.middleware.grouper.hooks.beans.HooksAttributeDefNameBean;
import edu.internet2.middleware.grouper.hooks.beans.HooksContext;
import edu.internet2.middleware.grouper.hooks.logic.GrouperHookType;
import edu.internet2.middleware.grouper.hooks.logic.GrouperHooksUtils;
import edu.internet2.middleware.grouper.hooks.logic.HookVeto;


/**
 * <pre>
 * built in hook to grouper, which is turned on when it is configured in the grouper.properties.
 * 
 * you can retrict certain attributes of a attribute def name to be within a certain regex
 * 
 * </pre>
 */
public class AttributeDefNameAttributeNameValidationHook extends AttributeDefNameHooks {
  
  /**
   * for unit tests
   */
  public static final String TEST_ATTRIBUTE_NAME = "testAttribute123";

  /**
   * for unit tests
   */
  public static final String TEST_PATTERN = "qwertyuiop";

  /**
   * only register once
   */
  private static boolean registered = false;
  
  /**
   * see if this is configured in the grouper.properties, if so, register this hook
   * @param addTestValidation if a test validation should be added
   */
  public static void registerHookIfNecessary(boolean addTestValidation) {
    
    if (registered) {
      return;
    }
    
    //see if there are config entries
    Properties grouperProperties = GrouperConfig.retrieveConfig().properties();

    
    //attributeDefName.attribute.validator.attributeName.0=extension
    //attributeDefName.attribute.validator.regex.0=^[a-zA-Z0-9]+$
    //attributeDefName.attribute.validator.vetoMessage.0=Attribute def name ID '$attributeValue$' is invalid since it must contain only alpha-numerics
    
    int index = 0;

    while (true) {
      String attributeName = StringUtils.trim(grouperProperties.getProperty(
          "attributeDefName.attribute.validator.attributeName." + index));
      String regex = StringUtils.trim(grouperProperties.getProperty(
          "attributeDefName.attribute.validator.regex." + index));
      String vetoMessage = StringUtils.trim(grouperProperties.getProperty(
          "attributeDefName.attribute.validator.vetoMessage." + index));
      
      //if we are done checking
      if (StringUtils.isBlank(attributeName)) {
        if (!StringUtils.isBlank(regex) || !StringUtils.isBlank(vetoMessage)) {
          throw new RuntimeException("Dont configure a regex or vetoMessage without an attribute name! index: " + index
              + ", check the grouper.properties");
        }
        break;
      }
      //these are required
      if (StringUtils.isBlank(regex) || StringUtils.isBlank(vetoMessage)) {
        throw new RuntimeException("Regex and vetoMessage are required for attribute: '" + attributeName
            + "' index: " + index + ", check the grouper.properties file");
      }
      //see if already exists
      if (attributeNamePatterns.containsKey(attributeName)) {
        throw new RuntimeException("Attribute name already exists (duplicate): '" + attributeName
            + "' index: " + index + ", check the grouper.properties file");
      }
      
      if (!"name".equals(attributeName) && !"extension".equals(attributeName) && !"displayName".equals(attributeName)
          && !"displayExtension".equals(attributeName) && !"description".equals(attributeName)) {
        
        throw new RuntimeException("Invalid attribute name '" + attributeName 
            + "', must be one of name, extension, displayExtension, displayName, description");
        
      }
      
      //add all configs
      attributeNamePatterns.put(attributeName, Pattern.compile(regex));
      attributeNameRegexes.put(attributeName, regex);
      attributeNameVetoMessages.put(attributeName, vetoMessage);
      
      index++;
    }

    if (addTestValidation) {
      
      attributeNamePatterns.put(TEST_ATTRIBUTE_NAME, Pattern.compile("^" + TEST_PATTERN + "$"));
      attributeNameRegexes.put(TEST_ATTRIBUTE_NAME, "^" + TEST_PATTERN + "$");
      attributeNameVetoMessages.put(TEST_ATTRIBUTE_NAME, "Attribute testAttribute123 cannot have the value: '$attributeValue$'");
      
      index++;
    }
    
    //register the hook
    if (index > 0) {
      //register this hook
      GrouperHooksUtils.addHookManual(GrouperHookType.ATTRIBUTE_DEF_NAME.getPropertyFileKey(), AttributeDefNameAttributeNameValidationHook.class);
    }
    
    registered = true;

  }
  
  /**
   * 
   * @see edu.internet2.middleware.grouper.hooks.AttributeDefNameHooks#attributeDefNamePreInsert(edu.internet2.middleware.grouper.hooks.beans.HooksContext, edu.internet2.middleware.grouper.hooks.beans.HooksGroupBean)
   */
  @Override
  public void attributeDefNamePreInsert(HooksContext hooksContext, HooksAttributeDefNameBean preInsertBean) {
    AttributeDefName attributeDefName = preInsertBean.getAttributeDefName();
    
    attributeDefNamePreChangeAttribute(attributeDefName);
  }

  /**
   * check that a new attribute value is ok
   * @param attributeDefName
   */
  private static void attributeDefNamePreChangeAttribute(AttributeDefName attributeDefName) {

    attributeDefNamePreChangeAttribute("description", attributeDefName.getDescription());
    attributeDefNamePreChangeAttribute("extension", attributeDefName.getExtension());
    attributeDefNamePreChangeAttribute("displayExtension", attributeDefName.getDisplayExtension());
    attributeDefNamePreChangeAttribute("name", attributeDefName.getName());
    attributeDefNamePreChangeAttribute("displayName", attributeDefName.getDisplayName());

  }

  /** cache of attribute names to patterns */
  static Map<String, Pattern> attributeNamePatterns = new HashMap<String, Pattern>();

  /** cache of attribute names to regexs */
  static Map<String, String> attributeNameRegexes = new HashMap<String, String>();

  /** cache of attribute names to error messages when a name doesnt match */
  static Map<String, String> attributeNameVetoMessages = new HashMap<String, String>();
  
  /**
   * check that a new attribute value is ok (either a attributeDefName field, or an attribute)
   * @param attributeName 
   * @param attributeValue 
   */
  static void attributeDefNamePreChangeAttribute(String attributeName, String attributeValue) {
    //see if there is a configuration about this attribute
    if (attributeNamePatterns.containsKey(attributeName)) {
      Pattern pattern = attributeNamePatterns.get(attributeName);
      if (pattern == null) {
        throw new RuntimeException("Regex pattern '" + attributeNameRegexes.get(attributeName) 
            + "'probably didnt compile for attribute: '" 
            + attributeName + "', check logs or grouper.properties");
      }
      Matcher matcher = pattern.matcher(StringUtils.defaultString(attributeValue));
      if (!matcher.matches()) {
        
        String attributeNameErrorMessage = attributeNameVetoMessages.get(attributeName);

        //substitute the attribute name
        attributeNameErrorMessage = StringUtils.replace(attributeNameErrorMessage, "$attributeValue$", attributeValue);
        
        throw new HookVeto("veto.attributeDefName.attribute.name.regex." + attributeName, attributeNameErrorMessage);
      }
    }
  }
  
  /**
   * @see edu.internet2.middleware.grouper.hooks.GroupHooks#groupPreUpdate(edu.internet2.middleware.grouper.hooks.beans.HooksContext, edu.internet2.middleware.grouper.hooks.beans.HooksGroupBean)
   */
  @Override
  public void attributeDefNamePreUpdate(HooksContext hooksContext, HooksAttributeDefNameBean preUpdateBean) {
    AttributeDefName attributeDefName = preUpdateBean.getAttributeDefName();
    attributeDefNamePreChangeAttribute(attributeDefName);
  }
  
}
