package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder

class MappedPropertyExpression extends ExpressionBase {
  
  def propertyMappingKey
  def builder
  
  MappedPropertyExpression(_propertyMappingKey, RelationalScopeBuilder _builder) {
    propertyMappingKey = _propertyMappingKey
    builder = _builder
  }
  
  def propertyFor(options) {
    options.propertyMappings[propertyMappingKey]
  }
  
  def call = { Map args ->
    builder._processArgs_(this, args)
  }
  
}