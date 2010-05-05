package com.radiadesign.relationalscope

import org.hibernate.criterion.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class NotRelationalScope extends OrRelationalScope {
  
  // --------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------
  
  NotRelationalScope(DefaultGrailsDomainClass _grailsDomainClass) {
    super(_grailsDomainClass)
  }
  
  //NotRelationalScope(JSONObject json) {
  //  super(json)
  //}
  
  protected NotRelationalScope(DefaultGrailsDomainClass _grailsDomainClass, ArrayList _scopes) {
    super(_grailsDomainClass, _scopes)
  }
  
  
  // --------------------------------------------------------------------------
  // Private API
  // --------------------------------------------------------------------------
  
  def instance() {
    new NotRelationalScope(grailsDomainClass)
  }
  
  Criterion toCriterion(criteria, associationPath, associationAliases) {
    Restrictions.not(super.toCriterion(criteria, associationPath, associationAliases))
  }
  
}