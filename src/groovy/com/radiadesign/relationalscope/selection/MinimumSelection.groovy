package com.radiadesign.relationalscope.selection

import org.hibernate.criterion.*

class MinimumSelection extends AbstractSelection {
    
  MinimumSelection(String _propertyName) {
    super(_propertyName)
  }
  
  Projection toProjection() {
    Projections.min(property)
  }
  
}