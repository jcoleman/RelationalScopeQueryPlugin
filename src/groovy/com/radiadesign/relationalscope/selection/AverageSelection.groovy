package com.radiadesign.relationalscope.selection

import org.hibernate.criterion.*

class AverageSelection extends AbstractSelection {
    
  AverageSelection(String _propertyName) {
    super(_propertyName)
  }
  
  Projection toProjection() {
    Projections.avg(property)
  }
  
}