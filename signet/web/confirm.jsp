<!--
  $Id: confirm.jsp,v 1.19 2005-07-12 23:13:26 acohen Exp $
  $Date: 2005-07-12 23:13:26 $
  
  Copyright 2004 Internet2 and Stanford University.  All Rights Reserved.
  Licensed under the Signet License, Version 1,
  see doc/license.txt in this distribution.
-->

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="robots" content="noindex, nofollow" />
    <title>
      Signet
    </title>
    <link href="styles/signet.css" rel="stylesheet" type="text/css" />
    <script language="JavaScript" type="text/javascript" src="scripts/signet.js">
    </script>
  </head>

  <body>
  
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<%@ page import="java.text.DateFormat" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.SortedSet" %>
<%@ page import="java.util.TreeSet" %>

<%@ page import="edu.internet2.middleware.signet.PrivilegedSubject" %>
<%@ page import="edu.internet2.middleware.signet.Subsystem" %>
<%@ page import="edu.internet2.middleware.signet.Category" %>
<%@ page import="edu.internet2.middleware.signet.Assignment" %>
<%@ page import="edu.internet2.middleware.signet.Function" %>
<%@ page import="edu.internet2.middleware.signet.tree.TreeNode" %>
<%@ page import="edu.internet2.middleware.signet.Signet" %>
<%@ page import="edu.internet2.middleware.signet.Limit" %>
<%@ page import="edu.internet2.middleware.signet.LimitValue" %>

<%@ page import="edu.internet2.middleware.signet.ui.Common" %>

<% 
  Signet signet
     = (Signet)
         (request.getSession().getAttribute("signet"));
   
  PrivilegedSubject loggedInPrivilegedSubject
     = (PrivilegedSubject)
         (request.getSession().getAttribute("loggedInPrivilegedSubject"));
   
   PrivilegedSubject currentGranteePrivilegedSubject
     = (PrivilegedSubject)
         (request.getSession().getAttribute("currentGranteePrivilegedSubject"));
         
   Subsystem currentSubsystem
     = (Subsystem)
         (request.getSession().getAttribute("currentSubsystem"));
         
   Category currentCategory
     = (Category)
         (request.getSession().getAttribute("currentCategory"));
         
   Function currentFunction
     = (Function)
         (request.getSession().getAttribute("currentFunction"));
         
   TreeNode currentScope
     = (TreeNode)
         (request.getSession().getAttribute("currentScope"));
         
   Assignment currentAssignment
     = (Assignment)
         (request.getSession().getAttribute("currentAssignment"));
         
   DateFormat dateFormat = DateFormat.getDateInstance();
   
   String personViewHref
     = "PersonView.do?granteeSubjectTypeId="
       + currentGranteePrivilegedSubject.getSubjectTypeId()
       + "&granteeSubjectId="
       + currentGranteePrivilegedSubject.getSubjectId()
       + "&subsystemId="
       + currentSubsystem.getId();
       
   String functionsHref
     = "Functions.do?select="
       + currentSubsystem.getId();
%>

    <form
      name="form1"
      method="post"
      action="" 
      onsubmit ="return checkForCursorInPersonSearch()">
    
      <tiles:insert page="/tiles/header.jsp" flush="true" />
      <div id="Navbar">
        <span class="logout">
          <a href="NotYetImplemented.do">
            <%=loggedInPrivilegedSubject.getName()%>: Logout
          </a>
        </span> <!-- logout -->
        <span class="select">
          <a href="Start.do">
            Home
          </a>
          &gt; <!-- displays as text right-angle bracket -->
          <a href="<%=personViewHref%>">  
            <%=currentGranteePrivilegedSubject.getName()%>
          </a>
          &gt; Grant new privilege
        </span> <!-- select -->
      </div>  <!-- Navbar -->
      
      <div id="Layout">
        <div id="Content">
          <div id="ViewHead">
            Privilege granted to
            <h1>
              <%=currentGranteePrivilegedSubject.getName()%>
            </h1>
            <span class="dropback"><%=currentGranteePrivilegedSubject.getDescription()%></span><!--,  Technology Strategy and Support Operations-->
          </div> <!-- ViewHead -->
           
          <div class="section">
				 		<h2>Completed assignment</h2>
							<table>
              	<tr>
              		<th width="15%" class="label" scope="row">Granted to:</td>
              		<td width="75%"><%=currentGranteePrivilegedSubject.getName()%></td>
             		</tr>
              	<tr>
              		<th class="label" scope="row">Type:</td>
              		<td><%=currentSubsystem.getName()%></td>
             		</tr>								
              	<tr>
              		<th class="label" scope="row">Function:</td>
              		<td>
										<span class="category"><%=currentCategory.getName()%></span> : 
				            <span class="function"><%=currentFunction.getName()%></span>
									</td>
             		</tr>
              	<tr>
              		<th class="label" scope="row">Scope:                
              		<td><ul class="none">
              <%=signet.displayAncestry
                    (currentScope,
                     "<ul class=\"arrow\">\n",  // childSeparatorPrefix
                     "\n<li>\n",                // levelPrefix
                     "\n</li>\n",               // levelSuffix
                     "\n</ul>")                 // childSeparatorSuffix
                 %>
            				</ul>
								</td>

<!-- Limits rows are generated by this next section -->								
								<%
  Limit[] limits
    = Common.getLimitsInDisplayOrder
        (currentAssignment.getFunction().getLimits());
  LimitValue[] limitValues
  	= Common.getLimitValuesInDisplayOrder(currentAssignment);
  for (int limitIndex = 0; limitIndex < limits.length; limitIndex++)
  {
    Limit limit = limits[limitIndex];
%>
              <tr>
                <td align="right">
                  <%=limit.getName()%>:
                </td>
                <td>
<%
    int limitValuesPrinted = 0;
    for (int limitValueIndex = 0;
         limitValueIndex < limitValues.length;
         limitValueIndex++)
    {
      LimitValue limitValue = limitValues[limitValueIndex];
      if (limitValue.getLimit().equals(limit))
      {
%>
                  <%=(limitValuesPrinted++ > 0) ? "<br />" : ""%>
                  <%=limitValue.getDisplayValue()%>
<%
      }
    }
%>
               
                </td>
              </tr>
<%
  }
%>

<!-- Limits rows complete -->
								
							<tr>
                <th class="label" scope="row">
                  Privilege holder can:
                </td>
                <td>
                  <%=(currentAssignment.isGrantOnly() ? "" : "use this privilege")%>
                  <br />
                  <%=(currentAssignment.isGrantable() ? "grant this privilege to others" : "")%>
                </td>
              </tr>
							<tr>
								<th class="label" scope="row">Granted on: 
								<td>&nbsp;</td>
							</tr>
							<tr>
								<th class="label" scope="row">Granted by: 
								<td>&nbsp;</td>
							</tr>

							</table>						
				 </div><!-- section -->
				 
<div class="section">
          <h2>
             Continue
        </h2>
             <p>
               <a href="<%=personViewHref%>">
                 <img src="images/arrow_right.gif" alt="" />View all <%=currentGranteePrivilegedSubject.getName()%>'s privileges
               </a>
             </p>
             <p>
               <a href="Functions.do?select=<%=currentSubsystem.getId()%>">
                 <img src="images/arrow_right.gif" alt="" />Grant another privilege to <%=currentGranteePrivilegedSubject.getName()%>
               </a>
             </p>
             <p>
               <a href="Start.do">
                 <img src="images/arrow_right.gif" alt="" />Return to home page
               </a>
             </p>
          </div>
           <!-- section -->

        </div> <!-- Content -->    
         <div id="Sidebar">
          <div class="findperson">
            <h2>
              Find a subject </h2>
            <p>
              <input
                name="words"
                type="text"
                class="short"
                id="words"
                style="width:100px"
                size="15"
                maxlength="500"
                onfocus="personSearchFieldHasFocus=true;"
                onblur="personSearchFieldHasFocus=false;" />
              <input
                name="searchbutton"
                type="button"
                class="button1"
                onclick="javascript:loadXMLDoc('personQuickSearch.jsp?searchString=' + document.getElementById('words').value);"
                value="Search"
                onfocus="personSearchButtonHasFocus=true;"
                onblur="personSearchButtonHasFocus=false;" />
              <br />
              <label for="words">
                Enter a subject's name, and click "Search."
              </label>
            </p>
            <div id="PersonSearchResults" style="display:none">
            </div> <!-- PersonSearchResults -->
          </div> <!-- findperson -->

          <div class="views">
            <h2>
              View privileges...
            </h2>
            <p>
              <a href="Start.do">
            <img src="images/arrow_right.gif" alt="" />you have granted</a></p>
            <p>
              <a href="PersonView.do?granteeSubjectTypeId=<%=loggedInPrivilegedSubject.getSubjectTypeId()%>&granteeSubjectId=<%=loggedInPrivilegedSubject.getSubjectId()%>&subsystemId=<%=currentSubsystem.getId()%>">
                <img src="images/arrow_right.gif" alt="" />assigned to you</a>
            </p>
            <p>
              <a href="NotYetImplemented.do">
                <img src="images/arrow_right.gif" alt="" />by scope
              </a>
            </p>
          </div> <!-- views -->
                  
          <div class="helpbox">
            <h2>Help</h2>
            <jsp:include page="confirm-help.jsp" flush="true" />          
          </div>  <!-- end helpbox -->
         </div> <!-- Sidebar -->
         <tiles:insert page="/tiles/footer.jsp" flush="true" />
      </div> <!-- Layout -->
    </form>
  </body>
</html>
