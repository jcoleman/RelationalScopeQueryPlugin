package com.radiadesign.relationalscope.selection

import com.radiadesign.relationalscope.RelationalScope
import org.hibernate.criterion.*

class AbstractSelection {
  
  def property
  
  AbstractSelection(_property) {
    property = _property
  }
  
  static propertyFor(options, propertyKey) {
    def alias
    def associationPath
    
    // Determine if an association...
    def lastDotIndex = propertyKey.lastIndexOf('.')
    if (lastDotIndex != -1) {
      if (lastDotIndex == 0 || lastDotIndex == propertyKey.size() - 1) {
        throw new RuntimeException("Selected property string cannot start or end with a dot, was: '${propertyKey}'")
      }
      
      associationPath = propertyKey[0..lastDotIndex-1]
      propertyKey = propertyKey[lastDotIndex+1..-1]
    }
    
    if (associationPath) {
      RelationalScope.createAssociationAliasIfNecessary(options, associationPath)
      
      def discriminator = RelationalScope.aliasDiscriminatorFor(options)
      def discriminatedAliases = options.associationAliases[discriminator]
      assert discriminatedAliases : "An association was used for which no alias has been created"
      alias = discriminatedAliases[associationPath]
      assert alias : "An association was used for which no alias has been created"
    }
    return "${alias ?: options.currentRootAlias}.${propertyKey}"
  }
  
  Projection toProjection(options) {
    throw new RuntimeException("Please implement toProjection(options) in the extending class.")
  }
  
}