package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder

class MappedPropertyExpression extends AbstractPropertyExpressionBase {
  
  MappedPropertyExpression(_propertyMappingKey, RelationalScopeBuilder _builder) {
    super(_propertyMappingKey, _builder)
  }
  
  def propertyFor(options) {
    options.propertyMappings[propertyKey]
  }
  
}