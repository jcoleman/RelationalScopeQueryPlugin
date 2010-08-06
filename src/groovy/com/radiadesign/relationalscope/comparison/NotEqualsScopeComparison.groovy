package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.expression.*

class NotEqualsScopeComparison extends ScopeComparisonBase {
  
  NotEqualsScopeComparison(_lhsValue, _rhsValue) {
    super(_lhsValue, _rhsValue)
  }
  
  Criterion criterionForPropertyAndProperty(lhs, rhs, options) {
    Restrictions.disjunction()
                .add( Restrictions.neProperty(lhs, rhs) )
                .add( Restrictions.conjunction().add( Restrictions.isNull(lhs) )
                                                .add( Restrictions.isNotNull(rhs) ) )
                .add( Restrictions.conjunction().add( Restrictions.isNotNull(lhs) )
                                                .add( Restrictions.isNull(rhs) ) )
    
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    if (value == null) {
      Restrictions.isNotNull(property)
    } else {
      Restrictions.disjunction()
                  .add( Restrictions.ne(property, value) )
                  .add( Restrictions.isNull(property) )
    }
    
  }
  
  Criterion criterionForValueAndProperty(value, property, options) {
    criterionForPropertyAndValue(property, value, options)
  }
  
  Criterion criterionForPropertyAndSubquery(property, criteria, options) {
    Subqueries.propertyNe(property, criteria)
  }
  
  Criterion criterionForValueAndSubquery(value, criteria, options) {
    Subqueries.ne(value, criteria)
  }
  
  String toString() {
    return "(${lhsValue} != ${rhsValue})"
  }
  
}