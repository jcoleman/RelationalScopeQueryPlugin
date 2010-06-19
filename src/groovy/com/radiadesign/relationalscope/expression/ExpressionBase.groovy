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
  
}