package com.radiadesign.relationalscope.selection

import org.hibernate.criterion.*

class DistinctSelection extends AbstractSelection {
  
  DistinctSelection(AbstractSelection _property) {
    super(_property)
  }
  
  Projection toProjection() {
    Projections.distinct(property.toProjection())
  }
  
}