/*
  Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2006 The University Of Chicago

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

package edu.internet2.middleware.grouper;
import  java.util.Date;
import  java.util.LinkedHashSet;
import  java.util.Set;
import  net.sf.hibernate.*;

/**
 * Stub Hibernate {@link Stem} DAO.
 * <p/>
 * @author  blair christensen.
 * @version $Id: HibernateStemDAO.java,v 1.2 2006-12-19 18:56:44 blair Exp $
 * @since   1.2.0
 */
class HibernateStemDAO {

  // PRIVATE CLASS CONSTANTS //
  private static final String KLASS = HibernateStemDAO.class.getName();


  // PROTECTED CLASS METHODS //

  // @since   1.2.0
  protected static Set findAllByApproximateDisplayExtension(String val) {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery("from Stem as ns where ns.display_extension like :value");
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindByApproximateDisplayExtension");
      qry.setString(  "value" , "%" + val.toLowerCase() + "%" );
      stems.addAll( qry.list() );
      hs.close();
    }
    catch (HibernateException eH) {
      // TODO 20061219 this should throw some flavor of exception
      ErrorLog.error( HibernateStemDAO.class, eH.getMessage() );
    }
    return stems;
  } // protected static Set findAllByApproximateDisplayExtension(val)

  // @since   1.2.0
  protected static Set findAllByApproximateDisplayName(String val) {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery("from Stem as ns where ns.display_name like :value");
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindByApproximateDisplayName");
      qry.setString(  "value" , "%" + val.toLowerCase() + "%" );
      stems.addAll( qry.list() );
      hs.close();
    }
    catch (HibernateException eH) {
      // TODO 20061219 this should throw some flavor of exception
      ErrorLog.error( HibernateStemDAO.class, eH.getMessage() );
    }
    return stems;
  } // protected static Set findAllByApproximateDisplayName(val)

  // @since   1.2.0
  protected static Set findAllByApproximateExtension(String val) {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery("from Stem as ns where ns.stem_extension like :value");
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindByApproximateExtension");
      qry.setString(  "value" , "%" + val.toLowerCase() + "%" );
      stems.addAll( qry.list() );
      hs.close();
    }
    catch (HibernateException eH) {
      // TODO 20061219 this should throw some flavor of exception
      ErrorLog.error( HibernateStemDAO.class, eH.getMessage() );
    }
    return stems;
  } // protected static Set findAllByApproximateExtension(val)

  // @since   1.2.0
  protected static Set findAllByApproximateName(String val) {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery("from Stem as ns where ns.stem_name like :value");
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindByApproximateName");
      qry.setString(  "value" , "%" + val.toLowerCase() + "%" );
      stems.addAll( qry.list() );
      hs.close();
    }
    catch (HibernateException eH) {
      // TODO 20061219 this should throw some flavor of exception
      ErrorLog.error( HibernateStemDAO.class, eH.getMessage() );
    }
    return stems;
  } // protected static Set findAllByApproximateName(val)

  // @since   1.2.0
  protected static Set findAllByApproximateNameAny(String name) {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery(
        "from Stem as ns where "
        + "   lower(ns.stem_name)         like :name "
        + "or lower(ns.display_name)      like :name "
        + "or lower(ns.stem_extension)    like :name "
        + "or lower(ns.display_extension) like :name" 
      );
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindAllByApproximateNameAny");
      qry.setString("name", "%" + name.toLowerCase() + "%");
      stems.addAll( qry.list() );
      hs.close();
    }
    catch (HibernateException eH) {
      // TODO 20061219 this should throw some flavor of exception
      ErrorLog.error( HibernateStemDAO.class, eH.getMessage() );
    }
    return stems;
  } // protected static Set findAllByApproximateNameAny(name)

  // @since   1.2.0
  protected static Set findAllByCreatedAfter(Date d) {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery("from Stem as ns where ns.create_time > :time");
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindAllByCreatedAfter");
      qry.setLong( "time", d.getTime() );
      stems.addAll( qry.list() );
      hs.close();
    }
    catch (HibernateException eH) {
      // TODO 20061219 this should throw some flavor of exception
      ErrorLog.error( HibernateStemDAO.class, eH.getMessage() );
    }
    return stems;
  } // protected static Set findAllByCreatedAfter(d)

  // @since   1.2.0
  protected static Set findAllByCreatedBefore(Date d) {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery("from Stem as ns where ns.create_time < :time");
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindAllByCreatedBefore");
      qry.setLong( "time", d.getTime() );
      stems.addAll( qry.list() );
      hs.close();
    }
    catch (HibernateException eH) {
      // TODO 20061219 this should throw some flavor of exception
      ErrorLog.error( HibernateStemDAO.class, eH.getMessage() );
    }
    return stems;
  } // protected static Set findAllByCreatedBefore(d)

  // @since   1.2.0
  protected static Stem findByName(String name) 
    throws  StemNotFoundException
  {
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery("from Stem as ns where ns.stem_name = :name");
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindByName");
      qry.setString("name", name);
      Stem ns = (Stem) qry.uniqueResult();
      hs.close();
      if (ns == null) {
        throw new StemNotFoundException();
      }
      return ns;
    }
    catch (HibernateException eH) {
      throw new StemNotFoundException( eH.getMessage(), eH );
    }
  } // protected static Stem findByName(name)

  // @since   1.2.0
  protected static Stem findByUuid(String uuid)
    throws StemNotFoundException
  {
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery("from Stem as ns where ns.uuid = :uuid");
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindByUuid");
      qry.setString("uuid", uuid);
      Stem ns = (Stem) qry.uniqueResult();
      hs.close();
      if (ns == null) {
        throw new StemNotFoundException();
      }
      return ns; 
    }
    catch (HibernateException eH) {
      throw new StemNotFoundException( eH.getMessage(), eH );
    }
  } // protected static Stem findByUuid(uuid)

  // @since   1.2.0
  protected static Set findChildGroups(Stem ns) { // TODO 20061219 rename
    Set groups = new LinkedHashSet();
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery("from Group as g where g.parent_stem = :id");
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindChildGroups");
      qry.setString( "id", ns.getId() );
      groups.addAll( qry.list() );
      hs.close();
    }
    catch (HibernateException eH) {
      // TODO 20061215 this should throw some flavor of exception
      ErrorLog.error( HibernateStemDAO.class, eH.getMessage() );
    }
    return groups;
  } // protected sdtatic Set findChildGroups(ns)

  // @since   1.2.0
  protected static Set findChildStems(Stem ns) { // TODO 20601219 rename
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateHelper.getSession();
      Query   qry = hs.createQuery("from Stem as ns where ns.parent_stem = :id");
      qry.setCacheable(true);
      qry.setCacheRegion(KLASS + ".FindChildStems");
      qry.setString( "id", ns.getId() );
      stems.addAll( qry.list() );
      hs.close();
    }
    catch (HibernateException eH) {
      // TODO 20061215 this should throw some flavor of exception
      ErrorLog.error( HibernateStemDAO.class, eH.getMessage() );
    }
    return stems;
  } // protected sdtatic Set findChildStems(ns)

} // class HibernateStemDAO

