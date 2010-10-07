package com.radiadesign.relationalscope.selection

import org.hibernate.criterion.*

class CountSelection extends AbstractSelection {
    
  CountSelection(String _propertyName) {
    super(_propertyName)
  }
  
  CountSelection(DistinctSelection _property) {
    super(_property)
  }
  
  Projection toProjection() {
    if (property instanceof DistinctSelection) {
      if (property.property.property instanceof String) {
        Projections.countDistinct(property.property.property)
      } else {
        throw new RuntimeException("count(distinct(property)) expected property.property.property to be a String, got value ${property.property.property}")
      }
    } else if (property instanceof String) {
      Projections.count(property)
    }
  }
  
}