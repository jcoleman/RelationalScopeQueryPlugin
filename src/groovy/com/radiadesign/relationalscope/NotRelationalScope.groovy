package com.radiadesign.relationalscope

import org.hibernate.criterion.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class NotRelationalScope extends OrRelationalScope {
  
  // --------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------
  
  NotRelationalScope() {
    super()
  }
  
  NotRelationalScope(DefaultGrailsDomainClass _grailsDomainClass) {
    super(_grailsDomainClass)
  }
  
  //NotRelationalScope(JSONObject json) {
  //  super(json)
  //}
  
  private NotRelationalScope(DefaultGrailsDomainClass _grailsDomainClass, ArrayList _scopes) {
    super(_grailsDomainClass, _scopes)
  }
  
  
  // --------------------------------------------------------------------------
  // Private API
  // --------------------------------------------------------------------------
  
  def instance() {
    new NotRelationalScope()
  }
  
  Criterion toCriterion() {
    Restrictions.not(super.toCriterion())
  }
  
}