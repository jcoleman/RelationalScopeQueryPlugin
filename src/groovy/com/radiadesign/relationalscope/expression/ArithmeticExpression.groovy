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
  
  void appendSqlStringForHibernate(sqlWriter, criteria, criteriaQuery, options) {
    lhs.appendSqlStringForHibernate(sqlWriter, criteria, criteriaQuery, options)
    sqlWriter.append(' ')
    sqlWriter.append(operator)
    sqlWriter.append(' ')
    rhs.appendSqlStringForHibernate(sqlWriter, criteria, criteriaQuery, options)
  }
  
}