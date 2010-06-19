package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class LessThanScopeComparison extends ScopeComparisonBase {
  
  LessThanScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(options) {
    def property = AbstractPropertyExpressionBase.aliasedPropertyNameFor(options, propertyName)
    if (comparisonValue instanceof AbstractPropertyExpressionBase) {
      return Restrictions.ltProperty( property, comparisonValue.propertyFor(options) )
    } else {
      return Restrictions.lt(property, comparisonValue)
    }
  }
  
  String toString() {
    return "(${propertyName} < ${comparisonValue})"
  }
  
}