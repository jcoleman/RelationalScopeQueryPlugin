package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.RelationalScope

class ScopeComparisonBase {
  
  def propertyName
  def comparisonValue
  
  ScopeComparisonBase(String _propertyName, _comparisonValue) {
    propertyName = _propertyName
    comparisonValue = _comparisonValue
  }
  
  String fullPropertyNameFor(Map options, String propertyName) {
    def alias
    if (options.associationName) {
      def discriminator = RelationalScope.aliasDiscriminatorFor(options)
      alias = options.associationAliases[discriminator][options.associationName]
      assert alias : "An association was used for which no alias has been created"
    }
    return "${alias ?: options.currentRootAlias}.${propertyName}"
  }
  
  DetachedCriteria detachedCriteriaFor(RelationalScope comparisonValue, options) {
    options.incrementDetachedCriteriaCount()
    
    def rootAlias = "sqrt_${options.getDetachedCriteriaCount()}"
    def detachedCriteria = DetachedCriteria.forClass(comparisonValue.domainKlass, rootAlias)
    detachedCriteria.setProjection(Projections.id())
    def newOptions = options + [ criteria: detachedCriteria,
                                 associationName: comparisonValue.associationName,
                                 isDetachedCriteria: true,
                                 currentRootAlias: rootAlias ]
    detachedCriteria.add( comparisonValue.toCriterion(newOptions) )
    
  }
  
  Criterion toCriterion(criteria, associationPath, associationAliases) {
    assert false : "Should be implemented in the extending class."
  }
  
  String toString() {
    return "ScopeComparisonBase[propertyName: ${propertyName}, comparisonValue: ${comparisonValue}]"
  }
  
}