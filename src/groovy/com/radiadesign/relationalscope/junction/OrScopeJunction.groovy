package com.radiadesign.relationalscope.junction

import org.hibernate.criterion.*

class OrScopeJunction {
  
  def getJunction() {
    Restrictions.disjunction()
  }
  
  OrScopeJunction(scopes) {
    super(scopes)
  }
  
  String toString() {
    
  }
  
}