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
  
  Criterion toCriterion(options) {
    def originalWithinOrScopeFlag = options.withinOrScope
    options.withinOrScope = true
    
    Criterion criterion = super.toCriterion(options)
    
    // The weird indirection here setting it back to the original is to handle
    // the case where we have nested 'or' scopes. We don't want to unset the
    // flag until we exit the parent-most 'or' scope.
    options.withinOrScope = originalWithinOrScopeFlag
    return criterion
  }

  String toString() {
    "(${scopes.collect {it.toString()}.join(' || ')})"
  } 
  
  static junctionInspectString = 'or'

}