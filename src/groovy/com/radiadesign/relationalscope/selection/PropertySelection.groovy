package com.radiadesign.relationalscope.selection

import org.hibernate.criterion.*

class PropertySelection extends AbstractSelection {
    
  PropertySelection(String _propertyName) {
    super(_propertyName)
  }
  
  Projection toProjection(options) {
    Projections.property( AbstractSelection.propertyFor(options, property) )
  }
  
}