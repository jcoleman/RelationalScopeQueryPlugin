package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class PropertyMappingScopeComparison extends ScopeComparisonBase {
  
  PropertyMappingScopeComparison(_mappedValue, _mappingKey) {
    super(_mappedValue, _mappingKey)
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    options.propertyMappings[value] = property
    return null // Just a support comparison, no real criterion is generated here...
  }
  
  String toString() {
    return "(${lhsValue} => ${rhsValue})"
  }
  
}