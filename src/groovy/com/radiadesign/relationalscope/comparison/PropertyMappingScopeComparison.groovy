package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class PropertyMappingScopeComparison extends ScopeComparisonBase {
  
  PropertyMappingScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(options) {
    def property = fullPropertyNameFor(options, propertyName)
    options.propertyMappings[comparisonValue] = property
    return null
  }
  
  String toString() {
    return "(${propertyName} => ${comparisonValue})"
  }
  
}