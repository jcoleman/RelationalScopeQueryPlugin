package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.RelationalScope
import com.radiadesign.relationalscope.expression.*

class ScopeComparisonBase {
  
  def lhsValue
  def rhsValue
  
  ScopeComparisonBase(_lhsValue, _rhsValue) {
    lhsValue = _lhsValue
    rhsValue = _rhsValue
  }
  
  DetachedCriteria detachedCriteriaFor(RelationalScope scope, options) {
    options.incrementDetachedCriteriaCount()
    
    def rootAlias = "sqrt_${options.getDetachedCriteriaCount()}"
    def detachedCriteria = DetachedCriteria.forClass(scope.domainKlass, rootAlias)
    
    this.detachedCriteriaCallback(detachedCriteria)
    
    def newOptions = options + [ criteria: detachedCriteria,
                                 associationName: scope.associationName,
                                 isDetachedCriteria: true,
                                 currentRootAlias: rootAlias ]
    detachedCriteria.add( scope.toCriterion(newOptions) )
    
  }
  
  def detachedCriteriaCallback(criteria) {
    // Override in child class if desired...
  }
  
  Criterion _dispatchToCriterion_(AbstractPropertyExpression prop1, AbstractPropertyExpression prop2, options ) {
    this.criterionForPropertyAndProperty( prop1.propertyFor(options),
                                          prop2.propertyFor(options),
                                          options )
  }
  
  Criterion _dispatchToCriterion_(AbstractPropertyExpression prop, ValueExpression val, options) {
    this.criterionForPropertyAndValue(prop.propertyFor(options), val.value, options)
  }
  
  Criterion _dispatchToCriterion_(ValueExpression val, AbstractPropertyExpression prop, options) {
    _dispatchToCriterion_(prop, val, options)
  }
  
  Criterion _dispatchToCriterion_(AbstractPropertyExpression prop, RelationalScope scope, options) {
    this.criterionForPropertyAndSubquery( prop.propertyFor(options),
                                          this.detachedCriteriaFor(scope, options),
                                          options )
  }
  
  Criterion _dispatchToCriterion_(RelationalScope scope, AbstractPropertyExpression prop, options) {
    _dispatchToCriterion_(prop, scope, options)
  }
  
  Criterion _dispatchToCriterion_(ValueExpression val, RelationalScope scope, options) {
    this.criterionForValueAndSubquery( val.value,
                                       this.detachedCriteriaFor(scope, options),
                                       options )
  }
  
  Criterion _dispatchToCriterion_(RelationalScope scope, ValueExpression val, options) {
    _dispatchToCriterion_(val, scope, options)
  }
  
  Criterion toCriterion(options) {
    _dispatchToCriterion_(lhsValue, rhsValue, options)
  }
  
  Criterion criterionForPropertyAndProperty(lhs, rhs, options) {
    throw new RuntimeException("Property-to-Property comparator not implemented on ${this.class.simpleName}.")
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    throw new RuntimeException("Property-to-Value comparator not implemented on ${this.class.simpleName}.")
  }
  
  Criterion criterionForPropertyAndSubquery(property, criteria, options) {
    throw new RuntimeException("Property-to-Subquery comparator not implemented on ${this.class.simpleName}.")
  }
  
  Criterion criterionForValueAndSubquery(value, criteria, options) {
    throw new RuntimeException("Value-to-Subquery comparator not implemented on ${this.class.simpleName}.")
  }
  
  String toString() {
    return "ScopeComparisonBase[rhsValue: ${rhsValue}, lhsValue: ${lhsValue}]"
  }
  
}