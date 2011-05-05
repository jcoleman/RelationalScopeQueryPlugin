package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder

class ExpressionBase {
  
  def builder
  
  ExpressionBase(RelationalScopeBuilder _builder) {
    builder = _builder
  }
  
  def call = { Map args ->
    builder._processArgs_(this, args)
  }
  
  void appendSqlStringForHibernate(sqlWriter, criteria, criteriaQuery, options) {
    throw new RuntimeException("Please implement appendSqlStringForHibernate() in the extending class.")
  }
  
  ExpressionBase minus(ExpressionBase rhs) {
    return new ArithmeticExpression(this, rhs, '-', builder)
  }
  
  ExpressionBase minus(Number val) {
    def expr = new ValueExpression(val, builder)
    minus(expr)
  }
  
  ExpressionBase plus(ExpressionBase rhs) {
    return new ArithmeticExpression(this, rhs, '+', builder)
  }
  
  ExpressionBase plus(Number val) {
    def expr = new ValueExpression(val, builder)
    plus(expr)
  }
  
  ExpressionBase multiply(ExpressionBase rhs) {
    return new ArithmeticExpression(this, rhs, '*', builder)
  }
  
  ExpressionBase multiply(Number val) {
    def expr = new ValueExpression(val, builder)
    multiply(expr)
  }
  
  ExpressionBase div(ExpressionBase rhs) {
    return new ArithmeticExpression(this, rhs, '/', builder)
  }
  
  ExpressionBase div(Number val) {
    def expr = new ValueExpression(val, builder)
    div(expr)
  }
  
  ExpressionBase negative() {
    return new ArithmeticExpression(null, this, '-', builder)
  }
  
}