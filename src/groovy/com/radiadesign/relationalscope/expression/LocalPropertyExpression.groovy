package com.radiadesign.relationalscope.expression

import com.radiadesign.relationalscope.RelationalScopeBuilder
import com.radiadesign.relationalscope.RelationalScope

class LocalPropertyExpression extends AbstractPropertyExpression {
  
  LocalPropertyExpression(_propertyKey, RelationalScopeBuilder _builder) {
    super(_propertyKey, _builder)
  }
  
  def propertyFor(options) {
    def alias
    if (options.associationPath) {
      def discriminator = RelationalScope.aliasDiscriminatorFor(options)
      alias = options.associationAliases[discriminator][options.associationPath]
      assert alias : "An association was used for which no alias has been created"
    }
    return "${alias ?: options.currentRootAlias}.${propertyKey}"
  }
  
}