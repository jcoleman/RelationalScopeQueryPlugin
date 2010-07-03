package com.radiadesign.relationalscope.selection

import org.hibernate.criterion.*

class IdentifierSelection extends AbstractSelection {
    
  IdentifierSelection() {
    super(null)
  }
  
  Projection toProjection() {
    Projections.id()
  }
  
}