package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder
import com.radiadesign.relationalscope.RelationalScope
import com.radiadesign.hibernate.criterion.ArbitraryExpressionCriterion


class AbstractPropertyExpression extends ExpressionBase {
  
  def propertyKey
  
  AbstractPropertyExpression(_propertyKey, RelationalScopeBuilder _builder) {
    super(_builder)
    propertyKey = _propertyKey
  }
  
  def propertyFor(options) {
    throw new RuntimeException("Please implement propertyFor() in the extending class.")
  }
  
  String getSqlStringForHibernate(criteria, criteriaQuery, options) {
    return ArbitraryExpressionCriterion.getSingleColumnForPropertyName( propertyFor(options),
                                                               criteria,
                                                               criteriaQuery )
  }
  
}