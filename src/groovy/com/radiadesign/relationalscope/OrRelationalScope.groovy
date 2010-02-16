package com.radiadesign.relationalscope

import org.hibernate.criterion.*

class OrRelationalScope {
  
  // --------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------
  
  OrRelationalScope(Class _domain) {
    super(_domain)
  }
  
  //OrRelationalScope(JSONObject json) {
  //  super(json)
  //}
  
  private OrRelationalScope(Class _domain, ArrayList _scopes) {
    super(_domain, _scopes)
  }
  
  
  // --------------------------------------------------------------------------
  // Private API
  // --------------------------------------------------------------------------
  
  def junction() {
    Restrictions.disjunction()
  }
  
}