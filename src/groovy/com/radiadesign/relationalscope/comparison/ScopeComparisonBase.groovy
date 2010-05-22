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
    if (options.associationName) {
      def discriminator = RelationalScope.discriminatorFor(options)
      def alias = options.associationAliases[discriminator][options.associationName]
      assert alias : "An association was used for which no alias has been created"
      return "${alias}.${propertyName}"
    } else {
      return propertyName
    }
  }
  
  DetachedCriteria detachedCriteriaFor(RelationalScope comparisonValue, options) {
    options.incrementDetachedCriteriaCount()
    
    def detachedCriteria = DetachedCriteria.forClass(comparisonValue.domainKlass, "sqrt_${options.getDetachedCriteriaCount()}")
    detachedCriteria.setProjection(Projections.id())
    def newOptions = options + [criteria: detachedCriteria, associationName: comparisonValue.associationName, isDetachedCriteria: true]
    detachedCriteria.add( comparisonValue.toCriterion(newOptions) )
    
  }
  
  Criterion toCriterion(criteria, associationPath, associationAliases) {
    assert false : "Should be implemented in the extending class."
  }
  
  String toString() {
    return "ScopeComparisonBase[propertyName: ${propertyName}, comparisonValue: ${comparisonValue}]"
  }
  
}