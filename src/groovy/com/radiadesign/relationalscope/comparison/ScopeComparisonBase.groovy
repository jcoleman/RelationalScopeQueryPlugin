package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class ScopeComparisonBase {
  
  def propertyName
  def comparisonValue
  
  ScopeComparisonBase(String _propertyName, _comparisonValue) {
    propertyName = _propertyName
    comparisonValue = _comparisonValue
  }
  
  String fullPropertyNameFor(Map options, String propertyName) {
    if (options.associationName) {
      def alias = options.associationAliases[options.associationName]
      assert alias : "An association was used for which no alias has been created"
      return "${alias}.${propertyName}"
    } else {
      return propertyName
    }
  }
  
  Criterion toCriterion(criteria, associationPath, associationAliases) {
    assert false : "Should be implemented in the extending class."
  }
  
  String toString() {
    return "ScopeComparisonBase[propertyName: ${propertyName}, comparisonValue: ${comparisonValue}]"
  }
  
}