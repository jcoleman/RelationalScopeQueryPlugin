package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder
import com.radiadesign.relationalscope.RelationalScope

class ArithmeticExpression extends ExpressionBase {
  
  def lhs
  def rhs
  def operator
  
  ArithmeticExpression(_lhs, _rhs, _operator, RelationalScopeBuilder _builder) {
    super(_builder)
    lhs = _lhs
    rhs = _rhs
    operator = _operator
  }
  
  String getSqlStringForHibernate(criteria, criteriaQuery, options) {
    return ( '(' + lhs.getSqlStringForHibernate(criteria, criteriaQuery, options)
             + ' ' + operator + ' '
             + rhs.getSqlStringForHibernate(criteria, criteriaQuery, options) + ')' )
  }
  
}