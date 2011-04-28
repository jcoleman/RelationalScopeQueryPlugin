package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*
import com.radiadesign.relationalscope.RelationalScopeBuilder
import com.radiadesign.hibernate.criterion.ArbitraryExpressionCriterion

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
  
  Criterion  criterionForExpressionAndExpression(lhs, rhs, options) {
    if (!(rhs instanceof ValueExpression)) {
      throw new RuntimeException("Expected 'null' or 'notNull' for 'is' comparator; instead got '${rhs}'.")
    } else if (rhs.value == null) {
      new ArbitraryExpressionCriterion(lhs, null, 'IS NULL', options)
    } else if (rhs.value == RelationalScopeBuilder.notNull) {
      new ArbitraryExpressionCriterion(lhs, null, 'IS NOT NULL', options)
    } else {
      throw new RuntimeException("Invalid value (${value}) used in expression-to-expression comparator on ${this.class.simpleName}.")
    }
  }
  
  String toString() {
    return "(${lhsValue} == ${rhsValue})"
  }
  
}