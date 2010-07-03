package com.radiadesign.relationalscope.selection

import org.hibernate.criterion.*

class AbstractSelection {
  
  def property
  
  AbstractSelection(_property) {
    property = _property
  }
  
  Projection toProjection() {
    throw new RuntimeException("Please implement toProjection() in the extending class.")
  }
  
}