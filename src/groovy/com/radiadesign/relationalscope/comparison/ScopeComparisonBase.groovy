package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.RelationalScope
import com.radiadesign.relationalscope.expression.*

class ScopeComparisonBase {
  
  def propertyName
  def comparisonValue
  
  ScopeComparisonBase(_propertyName, _comparisonValue) {
    propertyName = _propertyName
    comparisonValue = _comparisonValue
  }
  
  DetachedCriteria detachedCriteriaFor(RelationalScope comparisonValue, options, criteriaCallback=null) {
    options.incrementDetachedCriteriaCount()
    
    def rootAlias = "sqrt_${options.getDetachedCriteriaCount()}"
    def detachedCriteria = DetachedCriteria.forClass(comparisonValue.domainKlass, rootAlias)
    
    criteriaCallback?.call(detachedCriteria)
    
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