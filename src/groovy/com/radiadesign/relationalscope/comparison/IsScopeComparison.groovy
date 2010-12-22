package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*
import com.radiadesign.relationalscope.RelationalScopeBuilder

class IsScopeComparison extends ScopeComparisonBase {
  
  IsScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndProperty(lhs, rhs, options) {
    Restrictions.eqProperty(lhs, rhs)
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    if (value == null) {
      Restrictions.isNull(property)
    } else if (value == RelationalScopeBuilder.notNull) {
      Restrictions.isNotNull(property)
    } else if (value == RelationalScopeBuilder.empty) {
      Restrictions.disjunction()
                  .add( Restrictions.eq(property, '') )
                  .add( Restrictions.isNull(property) )
    } else if (value == RelationalScopeBuilder.notEmpty) {
      Restrictions.conjunction()
                  .add( Restrictions.ne(property, '') )
                  .add( Restrictions.isNotNull(property) )
    } else {
      throw new RuntimeException("Invalid value (${value}) used in property-to-value comparator on ${this.class.simpleName}.")
    }
  }
  
  String toString() {
    return "(${lhsValue} == ${rhsValue})"
  }
  
}