package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class ScopeComparisonBase {
  
  def propertyName
  def comparisonValue
  
  ScopeComparisonBase(String _propertyName, _comparisonValue) {
    propertyName = _propertyName
    comparisonValue = _comparisonValue
  }
  
  Criterion toCriterion() {
    assert false : "Should be implemented in the extending class."
  }
  
  String toString() {
    return "ScopeComparisonBase[propertyName: ${propertyName}, comparisonValue: ${comparisonValue}]"
  }
  
}