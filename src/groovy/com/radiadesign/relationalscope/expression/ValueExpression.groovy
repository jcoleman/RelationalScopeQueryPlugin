package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder
import com.radiadesign.relationalscope.RelationalScope

class ValueExpression extends ExpressionBase {
  
  def value
  
  ValueExpression(_value, RelationalScopeBuilder _builder) {
    super(_builder)
    value = _value
  }
  
}