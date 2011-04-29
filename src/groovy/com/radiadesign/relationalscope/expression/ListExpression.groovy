package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder
import com.radiadesign.relationalscope.RelationalScope

class ListExpression extends ValueExpression {
  
  def containsOnlyPrimitives
  
  ListExpression(_value, _containsOnlyPrimitives, RelationalScopeBuilder _builder) {
    super(_value, _builder)
    containsOnlyPrimitives = _containsOnlyPrimitives
  }
  
}