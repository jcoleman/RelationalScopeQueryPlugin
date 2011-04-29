package com.radiadesign.relationalscope.selection

import com.radiadesign.relationalscope.RelationalScope
import org.hibernate.criterion.*

class AbstractSelection {
  
  def property
  
  AbstractSelection(_property) {
    property = _property
  }
  
  static propertyFor(options, propertyKey) {
    RelationalScope.propertyFor(options, propertyKey)
  }
  
  Projection toProjection(options) {
    throw new RuntimeException("Please implement toProjection(options) in the extending class.")
  }
  
}