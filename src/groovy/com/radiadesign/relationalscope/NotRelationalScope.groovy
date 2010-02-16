package com.radiadesign.relationalscope

import org.hibernate.criterion.*

class NotRelationalScope extends OrRelationalScope {
  
  // --------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------
  
  NotRelationalScope() {
    super()
  }
  
  NotRelationalScope(Class _domain) {
    super(_domain)
  }
  
  //NotRelationalScope(JSONObject json) {
  //  super(json)
  //}
  
  private NotRelationalScope(Class _domain, ArrayList _scopes) {
    super(_domain, _scopes)
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