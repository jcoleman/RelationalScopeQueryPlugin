package com.radiadesign.relationalscope.expression

class MappedPropertyExpression extends ExpressionBase {
  
  def propertyMappingKey
  
  MappedPropertyExpression(_propertyMappingKey) {
    propertyMappingKey = _propertyMappingKey
  }
  
  def propertyFor(options) {
    options.propertyMappings[propertyMappingKey]
  }
  
}