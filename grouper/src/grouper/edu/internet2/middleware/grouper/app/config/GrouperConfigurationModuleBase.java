package edu.internet2.middleware.grouper.app.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.cfg.dbConfig.CheckboxValueDriver;
import edu.internet2.middleware.grouper.cfg.dbConfig.ConfigFileName;
import edu.internet2.middleware.grouper.cfg.dbConfig.ConfigItemFormElement;
import edu.internet2.middleware.grouper.cfg.dbConfig.ConfigItemMetadata;
import edu.internet2.middleware.grouper.cfg.dbConfig.ConfigItemMetadataType;
import edu.internet2.middleware.grouper.cfg.dbConfig.ConfigSectionMetadata;
import edu.internet2.middleware.grouper.cfg.dbConfig.DbConfigEngine;
import edu.internet2.middleware.grouper.cfg.dbConfig.GrouperConfigHibernate;
import edu.internet2.middleware.grouper.cfg.dbConfig.OptionValueDriver;
import edu.internet2.middleware.grouper.cfg.text.GrouperTextContainer;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouperClient.collections.MultiKey;
import edu.internet2.middleware.grouperClient.config.ConfigPropertiesCascadeBase;

public abstract class GrouperConfigurationModuleBase {
  
  /** logger */
  private static final Log LOG = GrouperUtil.getLog(GrouperConfigurationModuleBase.class);
  
  /**
   * config id of the daemon
   */
  private String configId;
  
  /**
   * is the config enabled or not
   * @return
   */
  public boolean isEnabled() {
    return true;
  }
  
  /**
   * property suffix that will be used to identify the config eg class
   * @return
   */
  public String getPropertySuffixThatIdentifiesThisConfig() {
    return null;
  }
  
  /**
   * property value that identifies the config. Suffix is required for this property to be useful. eg: edu.internet2.middleware.grouper.app.ldapProvisioning.LdapSync
   * @return
   */
  public String getPropertyValueThatIdentifiesThisConfig() {
    return null;
  }
  
  /**
   * config id that identified this config. either suffix and value or getConfigIdThatIdentifiesThisConfig is required, not both. eg: personLdap
   * @return
   */
  public String getConfigIdThatIdentifiesThisConfig() {
    return null;
  }
  
  /**
   * extra configs that dont match the regex or prefix
   */
  protected Set<String> extraConfigKeys = new LinkedHashSet<String>();

  public Set<String> retrieveExtraConfigKeys() {
    return extraConfigKeys;
  }
  
  
  /**
   * list of systems that can be configured
   * @return
   */
  public static List<GrouperConfigurationModuleBase> retrieveAllConfigurationTypesHelper(Set<String> classNames) {
    
    List<GrouperConfigurationModuleBase> result = new ArrayList<GrouperConfigurationModuleBase>();
    
    for (String className: classNames) {

      try {
        Class<GrouperConfigurationModuleBase> configClass = (Class<GrouperConfigurationModuleBase>) GrouperUtil.forName(className);
        GrouperConfigurationModuleBase config = GrouperUtil.newInstance(configClass);
        result.add(config);
      } catch (Exception e) {
        //TODO ignore for now. for external systems we might not have all the classes on the classpath
      }
    }
    
    return result;
  }
  
  
  /**
   * get all configurations configured for this type
   * @return
   */
  protected List<GrouperConfigurationModuleBase> listAllConfigurationsOfThisType() {
    
    List<GrouperConfigurationModuleBase> result = new ArrayList<GrouperConfigurationModuleBase>();
    
    for (String configId : this.retrieveConfigurationConfigIds()) {
      
      @SuppressWarnings("unchecked")
      Class<GrouperConfigurationModuleBase> theClass = (Class<GrouperConfigurationModuleBase>)this.getClass();
      GrouperConfigurationModuleBase config = GrouperUtil.newInstance(theClass);
      config.setConfigId(configId);
      result.add(config);
    }
    
    return result;
  }
  
  /**
   * list of configured systems
   * @return
   */
  public static List<GrouperConfigurationModuleBase> retrieveAllConfigurations(Set<String> classNames) {
    
    List<GrouperConfigurationModuleBase> result = new ArrayList<GrouperConfigurationModuleBase>();
    
    for (String className: classNames) {
      try {        
        Class<GrouperConfigurationModuleBase> configClass = (Class<GrouperConfigurationModuleBase>) GrouperUtil.forName(className);
        GrouperConfigurationModuleBase config = GrouperUtil.newInstance(configClass);
        result.addAll(config.listAllConfigurationsOfThisType());
      } catch(Exception e) {
        //TODO ignore for now. for external systems we might not have all the classes on the classpath
      }
    }
    
    return result;
  }
  
  /**
   * call retrieveAttributes() to get this
   */
  protected Map<String, GrouperConfigurationModuleAttribute> attributeCache = null;
  
  /**
   * config id
   * @return
   */
  public String getConfigId() {
    return configId;
  }
  
  /**
   * config id
   * @param configId
   */
  public void setConfigId(String configId) {
    this.configId = configId;
  }

  /**
   * validations to run before saving values into db
   * @param isInsert
   * @param fromUi
   * @param errorsToDisplay
   * @param validationErrorsToDisplay
   */
  public void validatePreSave(boolean isInsert, boolean fromUi, List<String> errorsToDisplay, Map<String, String> validationErrorsToDisplay) {
    
    if (isInsert) {
      if (this.retrieveConfigurationConfigIds().contains(this.getConfigId())) {
        validationErrorsToDisplay.put("#configId", GrouperTextContainer.textOrNull("grouperConfigurationValidationConfigIdUsed"));
      }
      
      if (!isMultiple()) {
        validationErrorsToDisplay.put("#configId", GrouperTextContainer.textOrNull("grouperConfigurationValidationNotMultiple"));
      }
    }
    
    if (isMultiple()) {
      Pattern configIdPattern = Pattern.compile("^[a-zA-Z0-9_]+$");
      if (!configIdPattern.matcher(this.getConfigId()).matches()) {
        validationErrorsToDisplay.put("#configId", GrouperTextContainer.textOrNull("grouperConfigurationValidationConfigIdInvalid"));
      }
    }
    
    
    // first check if checked the el checkbox then make sure there's a script there
    {
      boolean foundElRequiredError = false;
      for (GrouperConfigurationModuleAttribute grouperConfigModuleAttribute : this.retrieveAttributes().values()) {
        
        if (grouperConfigModuleAttribute.isExpressionLanguage() && StringUtils.isBlank(grouperConfigModuleAttribute.getExpressionLanguageScript())) {
          
          GrouperTextContainer.assignThreadLocalVariable("configFieldLabel", grouperConfigModuleAttribute.getLabel());
          validationErrorsToDisplay.put(grouperConfigModuleAttribute.getHtmlForElementIdHandle(), 
              GrouperTextContainer.textOrNull("grouperConfigurationValidationElRequired"));
          GrouperTextContainer.resetThreadLocalVariableMap();
          foundElRequiredError = true;
        }
        
      }
      if (foundElRequiredError) {
        return;
      }
    }
    
    // types
    for (GrouperConfigurationModuleAttribute grouperConfigModuleAttribute : this.retrieveAttributes().values()) {
      
      GrouperTextContainer.assignThreadLocalVariable("configFieldLabel", grouperConfigModuleAttribute.getLabel());
      try {
        
      ConfigItemMetadataType configItemMetadataType = grouperConfigModuleAttribute.getConfigItemMetadata().getValueType();
      
      String value = null;
      
      try {
        value = grouperConfigModuleAttribute.getEvaluatedValueForValidation();
      } catch (UnsupportedOperationException uoe) {
        // ignore, it will get validated in the post-save
        continue;
      }
      
      // required
      if (StringUtils.isBlank(value)) {
        if (grouperConfigModuleAttribute.getConfigItemMetadata().isRequired() && grouperConfigModuleAttribute.isShow()) {

          validationErrorsToDisplay.put(grouperConfigModuleAttribute.getHtmlForElementIdHandle(), 
              GrouperTextContainer.textOrNull("grouperConfigurationValidationRequired"));
        }
        
        continue;
      }
      String[] valuesToValidate = null;
     
      if (grouperConfigModuleAttribute.getConfigItemMetadata().isMultiple()) {
        valuesToValidate = GrouperUtil.splitTrim(value, ",");
      } else {
        valuesToValidate = new String[] {value};
      }

      for (String theValue : valuesToValidate) {
        
        // validate types
        String externalizedTextKey = configItemMetadataType.validate(theValue);
        if (StringUtils.isNotBlank(externalizedTextKey)) {
          
          validationErrorsToDisplay.put(grouperConfigModuleAttribute.getHtmlForElementIdHandle(), 
              GrouperTextContainer.textOrNull(externalizedTextKey));
          
        } else {
          String mustExtendClass = grouperConfigModuleAttribute.getConfigItemMetadata().getMustExtendClass();
          if (StringUtils.isNotBlank(mustExtendClass)) {
            
            Class mustExtendKlass = GrouperUtil.forName(mustExtendClass);
            Class childClass = GrouperUtil.forName(theValue);
            
            if (!mustExtendKlass.isAssignableFrom(childClass)) {
              
              String error = GrouperTextContainer.textOrNull("grouperConfigurationValidationDoesNotExtendClass");
              error = error.replace("$$mustExtendClass$$", mustExtendClass);
              
              validationErrorsToDisplay.put(grouperConfigModuleAttribute.getHtmlForElementIdHandle(), error);
            }
          }
          
          String mustImplementInterface = grouperConfigModuleAttribute.getConfigItemMetadata().getMustImplementInterface();
          if (StringUtils.isNotBlank(mustImplementInterface)) {
            
            Class mustImplementInterfaceClass = GrouperUtil.forName(mustImplementInterface);
            Class childClass = GrouperUtil.forName(theValue);
            
            if (!mustImplementInterfaceClass.isAssignableFrom(childClass)) {
              
              String error = GrouperTextContainer.textOrNull("grouperConfigurationValidationDoesNotImplementInterface");
              error = error.replace("$$mustImplementInterface$$", mustImplementInterface);
              
              validationErrorsToDisplay.put(grouperConfigModuleAttribute.getHtmlForElementIdHandle(), error);
            }
          }
          
          
        }
      }
    } finally {
      GrouperTextContainer.resetThreadLocalVariableMap();
    }
    }
  }
  
  /**
   * retrieve attributes based on the instance
   * @return
   */
  public Map<String, GrouperConfigurationModuleAttribute> retrieveAttributes() {
    
    if (this.attributeCache != null) {
      return this.attributeCache;
    }
    
    ConfigFileName configFileName = this.getConfigFileName();
    
    if (configFileName == null) {
      throw new RuntimeException("configFileName cant be null for " + this.getClass().getName());
    }
    
    ConfigPropertiesCascadeBase configPropertiesCascadeBase = configFileName.getConfig();
    
    Map<String, GrouperConfigurationModuleAttribute> result = new LinkedHashMap<String, GrouperConfigurationModuleAttribute>();

    Pattern pattern = Pattern.compile(this.getConfigIdRegex());

    List<ConfigSectionMetadata> configSectionMetadataList = configFileName.configFileMetadata().getConfigSectionMetadataList();
    
    // get the attributes based on the configIdThatIdentifiesThisConfig
    String configIdThatIdentifiesThisConfig = null;
    
    if (this.isMultiple() && StringUtils.isBlank(this.getConfigId())) {
      throw new RuntimeException("Cant have isMultiple and a blank configId! " + this.getClass().getName());
    }
    if (!this.isMultiple() && !StringUtils.isBlank(this.getConfigId())) {
      throw new RuntimeException("Cant have not isMultiple and configId! " + this.getClass().getName());
    }

    if (this.getPropertySuffixThatIdentifiesThisConfig() != null) {
      
      if (StringUtils.isBlank(this.getPropertyValueThatIdentifiesThisConfig())) {
        throw new RuntimeException("getPropertyValueThatIdentifiesThisConfig is required for " + this.getClass().getName());
      }
      
      if (StringUtils.isNotBlank(this.getConfigIdThatIdentifiesThisConfig())) {
        throw new RuntimeException("can't specify ConfigIdThatIdentifiesThisConfig and PropertySuffixThatIdentifiesThisConfig for class "+this.getClass().getName());
      }
 
      if (!this.isMultiple()) {
        throw new RuntimeException("Cant have getPropertySuffixThatIdentifiesThisConfig and not be multiple! " + this.getClass().getName());
      }
      
      outer: for (ConfigSectionMetadata configSectionMetadata: configSectionMetadataList) {
        for (ConfigItemMetadata configItemMetadata: configSectionMetadata.getConfigItemMetadataList()) {
          
          Matcher matcher = pattern.matcher(configItemMetadata.getKeyOrSampleKey());
          if (!matcher.matches()) {
            continue;
          }
          
          String configId = matcher.group(2);
          String suffix = matcher.group(3);

          if (StringUtils.equals(suffix, this.getPropertySuffixThatIdentifiesThisConfig())) {
            
            if (StringUtils.equals(configItemMetadata.getValue(), this.getPropertyValueThatIdentifiesThisConfig())
                || StringUtils.equals(configItemMetadata.getSampleValue(), this.getPropertyValueThatIdentifiesThisConfig())) {
              configIdThatIdentifiesThisConfig = configId;
              break outer;
            }
            
          }
        }
      }
      
      if (StringUtils.isBlank(configIdThatIdentifiesThisConfig)) {
        throw new RuntimeException("can't find property in config file that identifies this daemon for " + this.getClass().getName());
      }
      
    } else if (this.getConfigIdThatIdentifiesThisConfig() != null ) {
      configIdThatIdentifiesThisConfig = this.getConfigIdThatIdentifiesThisConfig();
    }
    
      for (ConfigSectionMetadata configSectionMetadata: configSectionMetadataList) {
        for (ConfigItemMetadata configItemMetadata: configSectionMetadata.getConfigItemMetadataList()) {

          String propertyName = configItemMetadata.getKeyOrSampleKey();
          String suffix = propertyName;
          if (!this.retrieveExtraConfigKeys().contains(propertyName)) {
            
            Matcher matcher = pattern.matcher(configItemMetadata.getKeyOrSampleKey());
            if (!matcher.matches()) {
              continue;
            }
            
            String prefix = matcher.group(1);
            suffix = null;
                    
            if(this.isMultiple()) { // multiple means config id will not be blank on an edit

              if (StringUtils.isBlank(configIdThatIdentifiesThisConfig)) {
                throw new RuntimeException("Why is configIdThatIdentifiesThisConfig blank??? " + this.getClass().getName());
              }

              String currentConfigId = matcher.group(2);

              if (!StringUtils.equals(currentConfigId, configIdThatIdentifiesThisConfig)) {
                continue;
              }
              
              suffix = matcher.group(3);
              propertyName = prefix + "." + this.getConfigId() + "." + suffix;
              
            } else {
              
              if (!StringUtils.isBlank(this.getConfigId())) {
                throw new RuntimeException("Why is configId not blank??? " + this.getClass().getName());
              }
              suffix = matcher.group(2);
              propertyName = configItemMetadata.getKeyOrSampleKey();
              
            }

          }
          
          GrouperConfigurationModuleAttribute grouperConfigModuleAttribute = buildConfigurationModuleAttribute(propertyName, suffix, true, configItemMetadata, configPropertiesCascadeBase);
          result.put(suffix, grouperConfigModuleAttribute);
       
        }
      }
      
      Map<String, GrouperConfigurationModuleAttribute> extraAttributes = retrieveExtraAttributes(result);
      result.putAll(extraAttributes);
      
      this.attributeCache = result;


    return result;
    
  }
  
  /**
   * config file name to check for properties and metadata
   * @return
   */
  public abstract ConfigFileName getConfigFileName();
  
  /**
   * prefix for the properties eg: provisioner.
   * @return
   */
  public abstract String getConfigItemPrefix();
  
  /**
   * config id regeg eg: ^(provisioner)\\.([^.]+)\\.(.*)$
   * @return
   */
  public abstract String getConfigIdRegex();
  
  /**
   * retrieve suffix based on the property name
   * @param pattern
   * @param propertyName
   * @return
   */
  public String retrieveSuffix(Pattern pattern, String propertyName) {
    Matcher matcher = pattern.matcher(propertyName);
    
    if (!matcher.matches()) {
      return null;
    }
    
    String configId = this.getConfigId();
    if (StringUtils.isBlank(configId)) {
      throw new RuntimeException("Why is configId blank??? " + this.getClass().getName());
    }
    
    String configIdFromProperty = matcher.group(2);
    
    if (!StringUtils.equals(configId, configIdFromProperty)) {
      return null;
    }
    
    String suffix = matcher.group(3);
    return suffix;
  }
  
  
  /**
   * get subsections for the UI
   * @return
   */
  public List<GrouperConfigurationModuleSubSection> getSubSections() {
    
    List<GrouperConfigurationModuleSubSection> results = new ArrayList<GrouperConfigurationModuleSubSection>();
    
    Set<String> sectionLabelsUsed = new HashSet<String>();
    
    for (GrouperConfigurationModuleAttribute grouperConfigModuleAttribute : this.retrieveAttributes().values()) {
      
      String sectionLabel = grouperConfigModuleAttribute.getConfigItemMetadata().getSubSection();
      if (StringUtils.isBlank(sectionLabel)) {
        sectionLabel = "NULL";
      }
      if (sectionLabelsUsed.contains(sectionLabel)) {
        continue;
      }
      sectionLabelsUsed.add(sectionLabel);
      
      
      GrouperConfigurationModuleSubSection configurationSubSection = new GrouperConfigurationModuleSubSection();
      configurationSubSection.setConfiguration(this);
      configurationSubSection.setLabel(grouperConfigModuleAttribute.getConfigItemMetadata().getSubSection());
      results.add(configurationSubSection);
    }
    
    return results;
  }
  
  private Map<String, GrouperConfigurationModuleAttribute> retrieveExtraAttributes(Map<String, GrouperConfigurationModuleAttribute> attributesFromBaseConfig) {
    
    ConfigFileName configFileName = this.getConfigFileName();
    
    if (configFileName == null) {
      throw new RuntimeException("configFileName cant be null for " + this.getClass().getName());
    }
    
    ConfigPropertiesCascadeBase configPropertiesCascadeBase = configFileName.getConfig();
    
    Map<String, GrouperConfigurationModuleAttribute> result = new LinkedHashMap<String, GrouperConfigurationModuleAttribute>();
    
    Pattern pattern = null;
    try {
      pattern = Pattern.compile(this.getConfigIdRegex());
    } catch (Exception e) {
      // daemon might throw an error so ignore it.
    }
        
    for (String propertyName: configPropertiesCascadeBase.properties().stringPropertyNames()) {
      
      String suffix = retrieveSuffix(pattern, propertyName);
      
      if (StringUtils.isBlank(suffix)) {
        continue;
      }
      
      if (attributesFromBaseConfig.containsKey(suffix)) {
        GrouperConfigurationModuleAttribute attribute = attributesFromBaseConfig.get(suffix);
        if (GrouperConfigHibernate.isPasswordHelper(attribute.getConfigItemMetadata(), configPropertiesCascadeBase.propertyValueString(propertyName))) {
          attribute.setValue(GrouperConfigHibernate.ESCAPED_PASSWORD);
        } else {
          attribute.setValue(configPropertiesCascadeBase.propertyValueString(propertyName));
        }
        
      } else {
        
        ConfigItemMetadata configItemMetadata = new ConfigItemMetadata();
        configItemMetadata.setFormElement(ConfigItemFormElement.TEXT);
        configItemMetadata.setValueType(ConfigItemMetadataType.STRING);
        
        GrouperConfigurationModuleAttribute configModuleAttribute =
            buildConfigurationModuleAttribute(propertyName, suffix, false, configItemMetadata, configPropertiesCascadeBase);
        
        result.put(suffix, configModuleAttribute);
      }
      
    }
    
    return result;
     
  }
  
  private GrouperConfigurationModuleAttribute buildConfigurationModuleAttribute(
      String propertyName, String suffix, boolean useConfigItemMetadataValue,
      ConfigItemMetadata configItemMetadata, ConfigPropertiesCascadeBase configPropertiesCascadeBase) {
    
    GrouperConfigurationModuleAttribute grouperConfigModuleAttribute = new GrouperConfigurationModuleAttribute();
    grouperConfigModuleAttribute.setConfigItemMetadata(configItemMetadata);
    
    grouperConfigModuleAttribute.setFullPropertyName(propertyName);
    grouperConfigModuleAttribute.setGrouperConfigModule(this);
    
    {
      boolean hasExpressionLanguage = configPropertiesCascadeBase.hasExpressionLanguage(propertyName);
      grouperConfigModuleAttribute.setExpressionLanguage(hasExpressionLanguage);

      if (hasExpressionLanguage) {
        String rawExpressionLanguage = configPropertiesCascadeBase.rawExpressionLanguage(propertyName);
        grouperConfigModuleAttribute.setExpressionLanguageScript(rawExpressionLanguage);
      }
    }
    
    String value = configPropertiesCascadeBase.propertyValueString(propertyName);
    if (useConfigItemMetadataValue) {
      value = StringUtils.isBlank(value) ? configItemMetadata.getValue(): value;
      value = StringUtils.isBlank(value) ? configItemMetadata.getSampleValue(): value;
    }
    grouperConfigModuleAttribute.setValue(value);
    
    grouperConfigModuleAttribute.setConfigSuffix(suffix);
    
    {
      grouperConfigModuleAttribute.setReadOnly(configItemMetadata.isReadOnly());
      grouperConfigModuleAttribute.setType(configItemMetadata.getValueType());
      grouperConfigModuleAttribute.setDefaultValue(configItemMetadata.getDefaultValue());
      grouperConfigModuleAttribute.setPassword(configItemMetadata.isSensitive());
    }
    
    if (configItemMetadata.getFormElement() == ConfigItemFormElement.DROPDOWN) {
      
      if (GrouperUtil.length(configItemMetadata.getOptionValues()) > 0) {
        List<MultiKey> valuesAndLabels = new ArrayList<MultiKey>();
        valuesAndLabels.add(new MultiKey("", ""));
        for (String optionValue : configItemMetadata.getOptionValues()) {
          
          String label = GrouperTextContainer.textOrNull("config."
              + this.getClass().getSimpleName() + ".attribute.option." + grouperConfigModuleAttribute.getConfigSuffix() + "." + optionValue + ".label");
          label = StringUtils.defaultIfBlank(label, optionValue);
          
          MultiKey valueAndLabel = new MultiKey(optionValue, label);
          valuesAndLabels.add(valueAndLabel);
        }
        grouperConfigModuleAttribute.setDropdownValuesAndLabels(valuesAndLabels);
      }
      
      if (StringUtils.isNotBlank(configItemMetadata.getOptionValuesFromClass())) {
        
        String optionValueFromClassString = configItemMetadata.getOptionValuesFromClass();
        Class<OptionValueDriver> klass = GrouperUtil.forName(optionValueFromClassString);
        OptionValueDriver driver = GrouperUtil.newInstance(klass);
        List<MultiKey> valuesAndLabels = new ArrayList<MultiKey>();
        valuesAndLabels.add(new MultiKey("", ""));
        valuesAndLabels.addAll(driver.retrieveKeysAndLabels());
        grouperConfigModuleAttribute.setDropdownValuesAndLabels(valuesAndLabels);
        
      }
    }
    
    if (configItemMetadata.getFormElement() == ConfigItemFormElement.CHECKBOX) {
      String checkboxValueFromClassString = configItemMetadata.getCheckboxValuesFromClass();
      Class<CheckboxValueDriver> klass = GrouperUtil.forName(checkboxValueFromClassString);
      CheckboxValueDriver driver = GrouperUtil.newInstance(klass);
      List<MultiKey> checkboxAttributes = driver.retrieveCheckboxAttributes();
      grouperConfigModuleAttribute.setCheckboxAttributes(checkboxAttributes);
    }
    
    if (grouperConfigModuleAttribute.isPassword()) {
      grouperConfigModuleAttribute.setPassword(true);
      grouperConfigModuleAttribute.setFormElement(ConfigItemFormElement.PASSWORD);
    } else {
      ConfigItemFormElement configItemFormElement = configItemMetadata.getFormElement();
      if (configItemFormElement != null) {
        grouperConfigModuleAttribute.setFormElement(configItemFormElement);
      } else {
        // boolean is a drop down
        if (configItemMetadata.getValueType() == ConfigItemMetadataType.BOOLEAN) {
          
          grouperConfigModuleAttribute.setFormElement(ConfigItemFormElement.DROPDOWN);
  
          if (GrouperUtil.length(grouperConfigModuleAttribute.getDropdownValuesAndLabels()) == 0) {
            
            List<MultiKey> valuesAndLabels = new ArrayList<MultiKey>();
            valuesAndLabels.add(new MultiKey("", ""));
            
            String trueLabel = GrouperTextContainer.textOrNull("config." 
                + this.getClass().getSimpleName() + ".attribute.option." + grouperConfigModuleAttribute.getConfigSuffix() + ".trueLabel");
            
            trueLabel = GrouperUtil.defaultIfBlank(trueLabel, GrouperTextContainer.textOrNull("config.defaultTrueLabel"));
  
            String falseLabel = GrouperTextContainer.textOrNull("config." 
                + this.getClass().getSimpleName() + ".attribute.option." + grouperConfigModuleAttribute.getConfigSuffix() + ".falseLabel");
            
            falseLabel = GrouperUtil.defaultIfBlank(falseLabel, GrouperTextContainer.textOrNull("config.defaultFalseLabel"));
            
            valuesAndLabels.add(new MultiKey("true", trueLabel));
            valuesAndLabels.add(new MultiKey("false", falseLabel));
            grouperConfigModuleAttribute.setDropdownValuesAndLabels(valuesAndLabels);
          }
        } else if (GrouperUtil.length(grouperConfigModuleAttribute.getValue()) > 100) {
  
          grouperConfigModuleAttribute.setFormElement(ConfigItemFormElement.TEXTAREA);
  
        } else {
          grouperConfigModuleAttribute.setFormElement(ConfigItemFormElement.TEXT);
        }
      }
    }
    
    return grouperConfigModuleAttribute;
  }
  
  /**
   * 
   * @param suffix
   * @return
   */
  public Boolean showAttributeOverride(String suffix) {
    return null;
  }
  
  /**
   * save the attribute in an insert.  Note, if theres a failure, you should see if any made it
   * @param attributesToSave are the attributes from "retrieveAttributes" with values in there
   * if a value is blank, then dont save that one
   * @param errorsToDisplay call from ui: guiResponseJs.addAction(GuiScreenAction.newMessage(GuiMessageType.error, message.toString()));
   * @param validationErrorsToDisplay call from ui: guiResponseJs.addAction(GuiScreenAction.newValidationMessage(GuiMessageType.error, validationKey, 
   *      validationErrorsToDisplay.get(validationKey)));
   */
  public void insertConfig(boolean fromUi, 
      StringBuilder message, List<String> errorsToDisplay, Map<String, String> validationErrorsToDisplay) {
    
    validatePreSave(true, fromUi, errorsToDisplay, validationErrorsToDisplay);

    if (errorsToDisplay.size() > 0 || validationErrorsToDisplay.size() > 0) {
      return;
    }
    
    Pattern endOfStringNewlinePattern = Pattern.compile(".*<br[ ]*\\/?>$");
    
    // add all the possible ones
    Map<String, GrouperConfigurationModuleAttribute> attributes = this.retrieveAttributes();
    for (String suffix : attributes.keySet()) {
    
      GrouperConfigurationModuleAttribute grouperConfigModuleAttribute = attributes.get(suffix);
      
      if (grouperConfigModuleAttribute.isHasValue()) {
        
        StringBuilder localMessage = new StringBuilder();
        
        DbConfigEngine.configurationFileAddEditHelper2(this.getConfigFileName().getConfigFileName(),
            grouperConfigModuleAttribute.getFullPropertyName(),
            grouperConfigModuleAttribute.isExpressionLanguage() ? "true" : "false",
            grouperConfigModuleAttribute.isExpressionLanguage() ? grouperConfigModuleAttribute.getExpressionLanguageScript() : grouperConfigModuleAttribute.getValue(),
            grouperConfigModuleAttribute.isPassword(), localMessage, new Boolean[] {false},
            new Boolean[] {false}, fromUi, "Added from config editor", errorsToDisplay, validationErrorsToDisplay, false);
        
        if (localMessage.length() > 0) {
          if(message.length() > 0) {
            
            if (fromUi && !endOfStringNewlinePattern.matcher(message).matches()) {
              message.append("<br />\n");
            } else if (!fromUi && message.charAt(message.length()-1) != '\n') {
              message.append("\n");
            }
            message.append(localMessage);
          }
        }
      }
    }

    ConfigPropertiesCascadeBase.clearCache();
    this.attributeCache = null;
  }
  
  /**
   * delete config
   * @param fromUi
   */
  public void deleteConfig(boolean fromUi) {
    
    Map<String, GrouperConfigurationModuleAttribute> attributes = this.retrieveAttributes();
    
    for (GrouperConfigurationModuleAttribute attribute: attributes.values()) {
      String fullPropertyName = attribute.getFullPropertyName();
      DbConfigEngine.configurationFileItemDeleteHelper(this.getConfigFileName().name(), fullPropertyName, fromUi, true);
    }
    
    ConfigPropertiesCascadeBase.clearCache();
    this.attributeCache = null;
  }
  
  /**
   * save the attribute in an edit.  Note, if theres a failure, you should see if any made it
   * @param attributesFromUser are the attributes from "retrieveAttributes" with values in there
   * if a value is blank, then dont save that one (delete)
   * @param errorsToDisplay call from ui: guiResponseJs.addAction(GuiScreenAction.newMessage(GuiMessageType.error, message.toString()));
   * @param validationErrorsToDisplay call from ui: guiResponseJs.addAction(GuiScreenAction.newValidationMessage(GuiMessageType.error, validationKey, 
   *      validationErrorsToDisplay.get(validationKey)));
   */
  public void editConfig(boolean fromUi, StringBuilder message, List<String> errorsToDisplay, Map<String, String> validationErrorsToDisplay) {
    
    validatePreSave(false, fromUi, errorsToDisplay, validationErrorsToDisplay);

    if (errorsToDisplay.size() > 0 || validationErrorsToDisplay.size() > 0) {
      return;
    }
    
    Map<String, GrouperConfigurationModuleAttribute> attributes = this.retrieveAttributes();
    
    Set<String> propertyNamesToDelete = new HashSet<String>();

    // add all the possible ones
    for (String suffix : attributes.keySet()) {
    
      GrouperConfigurationModuleAttribute grouperConfigModuleAttribute = attributes.get(suffix);

      propertyNamesToDelete.add(grouperConfigModuleAttribute.getFullPropertyName());
      
    }

    // and all the ones we detect
    if (!StringUtils.isBlank(this.getConfigId())) {
      
      Set<String> configKeys = this.retrieveConfigurationKeysByPrefix(this.getConfigItemPrefix());
      
      if (GrouperUtil.length(configKeys) > 0) {
        propertyNamesToDelete.addAll(configKeys);
      }
    }
    
    Map<String, GrouperConfigurationModuleAttribute> attributesToSave = new HashMap<String, GrouperConfigurationModuleAttribute>();
    
    // remove the edited ones
    for (String suffix : attributes.keySet()) {
    
      GrouperConfigurationModuleAttribute grouperConfigModuleAttribute = attributes.get(suffix);
      
      if (grouperConfigModuleAttribute.isHasValue()) {
        propertyNamesToDelete.remove(grouperConfigModuleAttribute.getFullPropertyName());
        attributesToSave.put(suffix, grouperConfigModuleAttribute);
      }
    }
    // delete some
    for (String key : propertyNamesToDelete) {
      DbConfigEngine.configurationFileItemDeleteHelper(this.getConfigFileName().getConfigFileName(), key , fromUi, false);
    }

    Pattern endOfStringNewlinePattern = Pattern.compile(".*<br[ ]*\\/?>$");
    
    // add/edit all the possible ones
    for (String suffix : attributesToSave.keySet()) {
    
      GrouperConfigurationModuleAttribute grouperConfigModuleAttribute = attributesToSave.get(suffix);
      
      StringBuilder localMessage = new StringBuilder();
      
      DbConfigEngine.configurationFileAddEditHelper2(this.getConfigFileName().getConfigFileName(), 
          grouperConfigModuleAttribute.getFullPropertyName(), 
          grouperConfigModuleAttribute.isExpressionLanguage() ? "true" : "false", 
          grouperConfigModuleAttribute.isExpressionLanguage() ? grouperConfigModuleAttribute.getExpressionLanguageScript() : grouperConfigModuleAttribute.getValue(),
          grouperConfigModuleAttribute.isPassword(), localMessage, new Boolean[] {false},
          new Boolean[] {false}, fromUi, "Added from config editor", errorsToDisplay, validationErrorsToDisplay, false);
      
      if (localMessage.length() > 0) {
        if(message.length() > 0) {
          
          if (fromUi && !endOfStringNewlinePattern.matcher(message).matches()) {
            message.append("<br />\n");
          } else if (!fromUi && message.charAt(message.length()-1) != '\n') {
            message.append("\n");
          }
          message.append(localMessage);
        }
      }
    }

    ConfigPropertiesCascadeBase.clearCache();
    this.attributeCache = null;
  }
  
  /**
   * get configuration names configured by prefix 
   * @param prefix of config e.g. ldap.personLdap.
   * @return the list of configured keys
   */
  public Set<String> retrieveConfigurationKeysByPrefix(String prefix) {
    Set<String> result = new HashSet<String>();
    ConfigFileName configFileName = this.getConfigFileName();
    
    ConfigPropertiesCascadeBase configPropertiesCascadeBase = configFileName.getConfig();
    
    Properties properties = configPropertiesCascadeBase.properties();

    for (Object propertyNameObject : properties.keySet()) {
      String propertyName = (String)propertyNameObject;
      if (propertyName.startsWith(prefix)) {

        if (result.contains(propertyName)) {
          LOG.error("Config key '" + propertyName + "' is defined in '" + configFileName.getConfigFileName() + "' more than once!");
        } else {
          result.add(propertyName);
        }
      }
    }
    return result;
  }
  
  /**
   * get title of the grouper daemon configuration
   * @return
   */
  public String getTitle() {
    String title = GrouperTextContainer.textOrNull("config." + this.getClass().getSimpleName() + ".title");
    if (StringUtils.isBlank(title)) {
      return this.getClass().getSimpleName();
    }
    return title;
  }
  
  /**
   * get description of the external system
   * @return
   */
  public String getDescription() {
    String title = GrouperTextContainer.textOrNull("config." + this.getClass().getSimpleName() + ".description");
    if (StringUtils.isBlank(title)) {
      return this.getClass().getSimpleName();
    }
    return title;
  }
  
  /**
   * get a set of config ids
   * @return
   */
  public Set<String> retrieveConfigurationConfigIds() {
    
    String regex = this.getConfigIdRegex();
    
    if (StringUtils.isBlank(regex)) {
      throw new RuntimeException("Regex is reqired for " + this.getClass().getName());
    }
    
    Set<String> result = new TreeSet<String>();
    
    ConfigFileName configFileName = this.getConfigFileName();
    
    ConfigPropertiesCascadeBase configPropertiesCascadeBase = configFileName.getConfig();
    
    Properties properties = configPropertiesCascadeBase.properties();

    Pattern pattern = Pattern.compile(regex);
    
    for (Object propertyNameObject : properties.keySet()) {
      String propertyName = (String)propertyNameObject;
      String value = (String) properties.get(propertyNameObject);
      
      Matcher matcher = pattern.matcher(propertyName);
      
      if (!matcher.matches()) {
        continue;
      }

      String configId = matcher.group(2);
      String suffix = matcher.group(3);
      
      if (StringUtils.isNotBlank(this.getConfigIdThatIdentifiesThisConfig()) && StringUtils.equals(configId, this.getConfigIdThatIdentifiesThisConfig())) {
        result.add(configId);
      }
      
      if (StringUtils.isNotBlank(this.getPropertySuffixThatIdentifiesThisConfig()) && 
          StringUtils.isNotBlank(this.getPropertyValueThatIdentifiesThisConfig()) &&
          StringUtils.equals(suffix, this.getPropertySuffixThatIdentifiesThisConfig()) 
          && StringUtils.equals(value, this.getPropertyValueThatIdentifiesThisConfig())) {
        result.add(configId);
      }
      
      if (StringUtils.isBlank(this.getPropertySuffixThatIdentifiesThisConfig()) && 
          StringUtils.isBlank(this.getPropertyValueThatIdentifiesThisConfig())) {
        result.add(configId);
      }
      
    }
    return result;
  }


  /**
   * for each type of configuration this is the prefix for eg in subsections. only ui concern in external text config.
   * @return
   */
  protected abstract String getConfigurationTypePrefix();
  
  /**
   * can there be multiple instances of this config. for eg: LdapProvisionerConfig is true but for GrouperDaemonChangeLogRulesConfiguration is false
   * @return
   */
  public boolean isMultiple() {
    return true;
  }
  
}
