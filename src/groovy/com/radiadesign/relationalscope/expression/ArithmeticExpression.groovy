package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder
import com.radiadesign.relationalscope.RelationalScope

class ArithmeticExpression extends ExpressionBase {
  
  ExpressionBase lhs
  ExpressionBase rhs
  String operator
  
  ArithmeticExpression(ExpressionBase _lhs, ExpressionBase _rhs, String _operator, RelationalScopeBuilder _builder) {
    super(_builder)
    lhs = _lhs
    rhs = _rhs
    operator = _operator
  }
  
}