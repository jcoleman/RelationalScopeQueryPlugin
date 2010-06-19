package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder
import com.radiadesign.relationalscope.RelationalScope

class LocalPropertyExpression extends AbstractPropertyExpressionBase {
  
  LocalPropertyExpression(_propertyKey, RelationalScopeBuilder _builder) {
    super(_propertyKey, _builder)
  }
  
  def propertyFor(options) {
    fullPropertyNameFor(options, propertyKey)
  }
  
}