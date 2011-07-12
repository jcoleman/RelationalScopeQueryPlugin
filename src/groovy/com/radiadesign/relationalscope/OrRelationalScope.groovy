package com.radiadesign.relationalscope

import org.hibernate.criterion.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class OrRelationalScope extends RelationalScope {
  
  // --------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------
  
  OrRelationalScope(DefaultGrailsDomainClass _grailsDomainClass) {
    super(_grailsDomainClass)
  }
  
  //OrRelationalScope(JSONObject json) {
  //  super(json)
  //}
  
  protected OrRelationalScope(OrRelationalScope scope) {
    super(scope)
  }
  
  
  // --------------------------------------------------------------------------
  // Private API
  // --------------------------------------------------------------------------
  
  def instance() {
    new OrRelationalScope(grailsDomainClass)
  }
  
  def junction() {
    Restrictions.disjunction()
  }

  String toString() {
    "(${scopes.collect {it.toString()}.join(' || ')})"
  } 
  
  static junctionInspectString = 'or'

}