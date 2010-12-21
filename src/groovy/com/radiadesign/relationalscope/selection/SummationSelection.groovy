package com.radiadesign.relationalscope.selection

import org.hibernate.criterion.*

class SummationSelection extends AbstractSelection {
    
  SummationSelection(String _propertyName) {
    super(_propertyName)
  }
  
  Projection toProjection(options) {
    Projections.sum( AbstractSelection.propertyFor(options, property) )
  }
  
}