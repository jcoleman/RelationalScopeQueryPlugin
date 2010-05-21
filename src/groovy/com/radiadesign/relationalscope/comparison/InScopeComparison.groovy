package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*

class InScopeComparison extends ScopeComparisonBase {
  
  InScopeComparison(String _propertyName, _comparisonValue) {
    super(_propertyName, _comparisonValue)
  }
  
  Criterion toCriterion(criteria, associationPath, associationAliases) {
    def property = fullPropertyNameFor(associationAliases, associationPath, propertyName)
    return criterionFor(property, _comparisonValue)
  }
  
  def criterionFor(property, Object[] _comparisonValue) {
    return Restrictions.(property, _comparisonValue)
  }
  
  def criterionFor(property, Collection _comparisonValue) {
    return Restrictions.(property, _comparisonValue)
  }
  
  String toString() {
    return "(${propertyName} in ${comparisonValue})"
  }
  
}