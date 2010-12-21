package com.radiadesign.relationalscope.selection

import org.hibernate.criterion.*

class MaximumSelection extends AbstractSelection {
    
  MaximumSelection(String _propertyName) {
    super(_propertyName)
  }
  
  Projection toProjection(options) {
    Projections.max( AbstractSelection.propertyFor(options, property) )
  }
  
}