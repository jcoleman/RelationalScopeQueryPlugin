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
  
  Criterion toCriterion(options) {
    Restrictions.not( super.toCriterion(options) )
  }

  String toString() {
    "!(${scopes.collect {it.toString()}.join(' || ')})"
  } 
}