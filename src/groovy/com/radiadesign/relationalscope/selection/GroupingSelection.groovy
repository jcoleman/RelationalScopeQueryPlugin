package com.radiadesign.relationalscope.selection

import org.hibernate.criterion.*

class GroupingSelection extends AbstractSelection {
    
  GroupingSelection(String _propertyName) {
    super(_propertyName)
  }
  
  Projection toProjection() {
    Projections.groupProperty(property)
  }
  
}