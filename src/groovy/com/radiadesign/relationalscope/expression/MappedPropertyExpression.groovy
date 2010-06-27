package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder

class MappedPropertyExpression extends AbstractPropertyExpression {
  
  MappedPropertyExpression(_propertyMappingKey, RelationalScopeBuilder _builder) {
    super(_propertyMappingKey, _builder)
  }
  
  def propertyFor(options) {
    def property = options.propertyMappings[propertyKey]
    if (property) {
      return property
    } else {
      throw new RuntimeException("Mapping '${propertyKey}' does't (yet) exist in this query. Perhaps you have a typo in your relational query?" as String)
    }
  }
  
}