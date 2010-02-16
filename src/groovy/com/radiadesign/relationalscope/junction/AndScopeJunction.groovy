package com.radiadesign.relationalscope.junction

import org.hibernate.criterion.*

class AndScopeJunction {
  
  def getJunction() {
    Restrictions.conjunction()
  }
  
  AndScopeJunction(scopes) {
    super(scopes)
  }
  
  String toString() {
    
  }
  
}