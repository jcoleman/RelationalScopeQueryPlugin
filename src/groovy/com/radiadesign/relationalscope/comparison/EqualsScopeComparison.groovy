package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class EqualsScopeComparison extends ScopeComparisonBase {
  
  EqualsScopeComparison(_property, _comparisonValue) {
    super(_property, _comparisonValue)
  }
  
  Criterion toCriterion(options) {
    def property = AbstractPropertyExpressionBase.aliasedPropertyNameFor(options, propertyName)
    if (comparisonValue instanceof AbstractPropertyExpressionBase) {
      return Restrictions.eqProperty( property, comparisonValue.propertyFor(options) )
    } else {
      return Restrictions.eq(property, comparisonValue)
    }
  }
  
  String toString() {
    return "(${propertyName} == ${comparisonValue})"
  }
  
}