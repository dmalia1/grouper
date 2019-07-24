<%@ include file="../assetsJsp/commonTaglib.jsp"%>

            <div class="bread-header-container">
              <ul class="breadcrumb">
                  <li><a href="#" onclick="return guiV2link('operation=UiV2Main.indexMain');">${textContainer.text['myServicesHomeBreadcrumb'] }</a><span class="divider"><i class='fa fa-angle-right'></i></span></li>
                  <li><a href="#" onclick="return guiV2link('operation=UiV2Main.miscellaneous');">${textContainer.text['miscellaneousBreadcrumb'] }</a><span class="divider"><i class='fa fa-angle-right'></i></span></li>
                  <li class="active">${textContainer.text['miscellaneousConfigureBreadcrumb'] }</li>
              </ul>
                            
              <div class="page-header blue-gradient">
                <div class="row-fluid">
                  <div class="lead span9"><h1>${textContainer.text['miscellaneousConfigurationMainDescription'] }</h1></div>
                  <%-- c:if test="${grouperRequestContainer.deprovisioningContainer.allowedToDeprovision}">
                      <div class="span3" id="deprovisioningMainMoreActionsButtonContentsDivId">
                        <%@ include file="deprovisioningMainMoreActionsButtonContents.jsp" %>
                      </div>
                  </c:if --%>
                </div>
                <div class="row-fluid">
                  <div class="span12">
                    <p style="margin-top: -1em; margin-bottom: 1em">${textContainer.text['miscellaneousConfigurationMainSubtitle']}</p>
                  </div>
                </div>

                <div id="configuration-select-container">
                 <form id="configurationSelectForm" class="form-horizontal" method="post" action="UiV2Configure.configure" >
                   <div class="control-group">
                     <label class="control-label">${textContainer.text['configurationSelectConfigFile'] }</label>
                     <div class="controls">
                       <%-- --%>
                       <select id="configFileSelect" class="span4" name="configFile" 
                            onchange="ajax('../app/UiV2Configure.configureSelectFile', {formIds: 'configurationSelectForm'}); return false;"
                            <%--   onchange="return guiV2link('operation=UiV2Configure.configure', {optionalFormElementNamesToSend: 'configFile'});" --%>
                              >
                          <option value=""></option>
                          <option ${grouperRequestContainer.configurationContainer.configFileName == 'GROUPER_CACHE_PROPERTIES' ? 'selected="selected"' : '' } value="GROUPER_CACHE_PROPERTIES">grouper.cache.properties</option>
                          <option ${grouperRequestContainer.configurationContainer.configFileName == 'GROUPER_CLIENT_PROPERTIES' ? 'selected="selected"' : '' } value="GROUPER_CLIENT_PROPERTIES">grouper.client.properties</option>
                          <option ${grouperRequestContainer.configurationContainer.configFileName == 'GROUPER_LOADER_PROPERTIES' ? 'selected="selected"' : '' } value="GROUPER_LOADER_PROPERTIES">grouper-loader.properties</option>
                          <option ${grouperRequestContainer.configurationContainer.configFileName == 'GROUPER_PROPERTIES' ? 'selected="selected"' : '' } value="GROUPER_PROPERTIES">grouper.properties</option>
                          <option ${grouperRequestContainer.configurationContainer.configFileName == 'GROUPER_UI_PROPERTIES' ? 'selected="selected"' : '' } value="GROUPER_UI_PROPERTIES">grouper-ui.properties</option>
                          <option ${grouperRequestContainer.configurationContainer.configFileName == 'GROUPER_WS_PROPERTIES' ? 'selected="selected"' : '' } value="GROUPER_WS_PROPERTIES">grouper-ws.properties</option>
                          <option ${grouperRequestContainer.configurationContainer.configFileName == 'SUBJECT_PROPERTIES' ? 'selected="selected"' : '' } value="SUBJECT_PROPERTIES">subject.properties</option>
                       </select>
                       
                       <span class="help-block">${textContainer.text['configurationSelectConfigFileDescription'] }</span>
                     
                     </div>
                   </div>
                   
                  </form>
               
                </div>

              </div>
              
              <!-- a <div class="row-fluid">
                     
                    </div> -->
              
            </div>

            <div class="row-fluid" id="configurationMainDivId">
            
              <c:if test="${grouperRequestContainer.configurationContainer.configFileName != null}">
                <h3>${grouperRequestContainer.configurationContainer.configFileName.configFileName}</h3>


                  
                
                  <table class="table table-hover table-bordered table-striped table-condensed data-table table-bulk-update footable">
                    <thead>
                    
                      <c:forEach items="${grouperRequestContainer.configurationContainer.guiConfigFile.guiConfigSections}" var="guiConfigSection">
                        <tr>
                          <th colspan="3">
                            <h4 style="margin-top: 2em">${grouper:escapeHtml(guiConfigSection.configSectionMetadata.title) }</h4>
                            <c:if test="${! grouper:isBlank(guiConfigSection.configSectionMetadata.comment)}">
                              <p style="font-weight: normal;">${grouper:escapeHtml(guiConfigSection.configSectionMetadata.comment.trim()) }</p>
                            </c:if>
                          </th>
                        </tr>
                      <tr>
                        <th style="white-space: nowrap; background-color: white">
                          ${textContainer.text['configurationColumnPropertyName']}
                        </th>
                        <th style="white-space: nowrap; background-color: white">
                          ${textContainer.text['configurationColumnValue']}
                        </th>
                        <th style="white-space: nowrap; background-color: white">
                          ${textContainer.text['configurationColumnConfiguredIn']}
                        </th>
                      </tr>
                    </thead>

                    <tbody> <%-- configConftainer.propertyNames --%>
                      <c:forEach items="${guiConfigSection.guiConfigProperties}" var="guiConfigProperty">
                        <c:set value="${guiConfigProperty.configItemMetadata}" var="configItemMetadata" />
                        <tr>
                          <td style="vertical-align: top">
                            <b>${grouper:escapeHtml(configItemMetadata.keyOrSampleKey)}</b>
                            <c:if test="${guiConfigProperty.hasType}">
                              <br />
                              <span style="font-size: 90%">${textContainer.text['configurationTypeLabel']} ${grouper:escapeHtml(guiConfigProperty.type) }</span>
                            </c:if>
                            <c:if test="${configItemMetadata.multiple}">
                              <span style="font-size: 90%">${textContainer.text['configurationMultiple']}</span>
                            </c:if>
                            <c:if test="${! grouper:isBlank(configItemMetadata.mustExtendClass)}">
                              <span style="font-size: 90%">${textContainer.text['configurationMustExtendClass']} ${configItemMetadata.mustExtendClass }</span>
                            </c:if>
                            <c:if test="${! grouper:isBlank(configItemMetadata.mustImplementInterface)}">
                              <span style="font-size: 90%">${textContainer.text['configurationMustImplementInterface']} ${configItemMetadata.mustImplementInterface }</span>
                            </c:if>
                            
                          </td>
                          <td style="vertical-align: top">
                            <b>${grouper:escapeHtml(guiConfigProperty.propertyValue)}</b>
                            <c:if test="${guiConfigProperty.scriptlet}">
                              <br />
                              <span style="font-size: 90%">${textContainer.text['configurationElScriptletLabel']} ${grouper:escapeHtml(guiConfigProperty.scriptletForUi) }</span>
                            </c:if>
                            
                            <c:if test="${! grouper:isBlank(guiConfigProperty.unprocessedValueIfDifferent)}">
                              <br />
                              <span style="font-size: 90%">${textContainer.text['configurationUnprocessedValueIfDifferentLabel']} ${grouper:escapeHtml(guiConfigProperty.unprocessedValueIfDifferent) }</span>
                            </c:if>

                            
                            <c:if test="${! grouper:isBlank(guiConfigProperty.cronDescription)}">
                              <br />
                              <span style="font-size: 90%">${textContainer.text['configurationCronLabel']} ${grouper:escapeHtml(guiConfigProperty.cronDescription) }</span>
                            </c:if>

                            <c:if test="${! grouper:isBlank(configItemMetadata.sampleValue)}">
                              <br />
                              <span style="font-size: 90%">${textContainer.text['configurationSampleValueLabel']} ${grouper:escapeHtml(configItemMetadata.sampleValue) }</span>
                            </c:if>
                            <c:if test="${! grouper:isBlank(configItemMetadata.comment)}">
                              <br />
                              <span style="font-size: 90%">${grouper:escapeHtml(configItemMetadata.comment) }</span>
                            </c:if>
                          </td>
                          <td style="vertical-align: top; white-space: nowrap">
                            ${guiConfigProperty.valueFromWhere}
                            <c:if test="${! grouper:isBlank(guiConfigProperty.baseValueIfDifferent)}">
                              <br />
                              <span style="font-size: 90%">${textContainer.text['configurationBaseValueIfDifferent']} ${grouper:escapeHtml(guiConfigProperty.baseValueIfDifferent) }</span>
                            </c:if>
                          </td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </c:forEach>
                  </table>

              </c:if>
            </div>
