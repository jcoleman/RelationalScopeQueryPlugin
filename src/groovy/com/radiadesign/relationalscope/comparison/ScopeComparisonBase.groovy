package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.RelationalScope
import com.radiadesign.relationalscope.expression.*

class ScopeComparisonBase {
  
  def lhsValue
  def rhsValue
  
  ScopeComparisonBase() {
    
  }
  
  ScopeComparisonBase(_lhsValue, _rhsValue) {
    lhsValue = _lhsValue
    rhsValue = _rhsValue
  }
  
  
  // --------------------------------------------------------------------------
  // Standard RelationalScope criterion creation implementation
  // --------------------------------------------------------------------------
  
  Criterion toCriterion(options) {
    _dispatchToCriterion_(lhsValue, rhsValue, options)
  }
  
  
  // --------------------------------------------------------------------------
  // Criterion creation methods to override in an extending comparison class
  // --------------------------------------------------------------------------
  
  Criterion criterionForPropertyAndProperty(lhs, rhs, options) {
    throw new RuntimeException("Property-to-Property comparator not implemented on ${this.class.simpleName}.")
  }
  
  Criterion criterionForPropertyAndValue(property, value, options) {
    throw new RuntimeException("Property-to-Value comparator not implemented on ${this.class.simpleName}.")
  }
  
  Criterion criterionForValueAndProperty(value, property, options) {
    throw new RuntimeException("Value-to-Property comparator not implemented on ${this.class.simpleName}.")
  }
  
  Criterion criterionForPropertyAndSubquery(property, criteria, options) {
    throw new RuntimeException("Property-to-Subquery comparator not implemented on ${this.class.simpleName}.")
  }
  
  Criterion criterionForSubqueryAndProperty(criteria, property, options) {
    throw new RuntimeException("Subquery-to-property comparator not implemented on ${this.class.simpleName}.")
  }
  
  Criterion criterionForValueAndSubquery(value, criteria, options) {
    throw new RuntimeException("Value-to-Subquery comparator not implemented on ${this.class.simpleName}.")
  }
  
  Criterion criterionForSubqueryAndValue(criteria, value, options) {
    throw new RuntimeException("Subquery-to-Value comparator not implemented on ${this.class.simpleName}.")
  }
  
  Criterion criterionForExpressionAndExpression(lhs, rhs, options) {
    throw new RuntimeException("Expression-to-Expression comparator not implemented on ${this.class.simpleName}.")
  }
  
  
  // --------------------------------------------------------------------------
  // Semi-private API: supports subquery comparisons
  // --------------------------------------------------------------------------
  
  def detachedCriteriaCallback(RelationalScope scope, criteria) {
    // Override in child class if desired...
  }
  
  Criterion createCriterionForSubqueryReturning(RelationalScope scope, Map options, Closure returnGenerator) {
    options.incrementDetachedCriteriaCount()
    
    def rootAlias = "sqrt_${options.getDetachedCriteriaCount()}"
    def detachedCriteria = DetachedCriteria.forClass(scope.domainKlass, rootAlias)
    
    this.detachedCriteriaCallback(scope, detachedCriteria)
    
    def newOptions = options + [ criteria: detachedCriteria,
                                 associationPath: scope.associationName,
                                 associationAliases: [:],
                                 isDetachedCriteria: true,
                                 currentRootAlias: rootAlias ]
    
    def addedDescriptorToStack = !(scope.associationName ?: scope.virtualAssociationName)
    if (addedDescriptorToStack) {
      RelationalScope.addAssociationDescriptorToStack( newOptions,
                                                       null,
                                                       scope.grailsDomainClass,
                                                       null )
    }
    
    scope.prepareCriteria(detachedCriteria, newOptions)
    
    Criterion criterion = returnGenerator(detachedCriteria)
    
    if (addedDescriptorToStack) {
      newOptions.associationDescriptorStack.pop()
    }
    
    return criterion
  }
  
  
  // --------------------------------------------------------------------------
  // Private: these strongly typed methods support the standardization of the
  //          criterion creation methods.
  // --------------------------------------------------------------------------
  
  Criterion _dispatchToCriterion_(AbstractPropertyExpression prop1, AbstractPropertyExpression prop2, options ) {
    this.criterionForPropertyAndProperty( prop1.propertyFor(options),
                                          prop2.propertyFor(options),
                                          options )
  }
  
  Criterion _dispatchToCriterion_(AbstractPropertyExpression prop, ValueExpression val, options) {
    this.criterionForPropertyAndValue(prop.propertyFor(options), val.value, options)
  }
  
  Criterion _dispatchToCriterion_(ValueExpression val, AbstractPropertyExpression prop, options) {
    this.criterionForValueAndProperty(val.value, prop.propertyFor(options), options)
  }
  
  Criterion _dispatchToCriterion_(AbstractPropertyExpression prop, RelationalScope scope, options) {
    def property = prop.propertyFor(options)
    createCriterionForSubqueryReturning(scope, options) { detachedCriteria ->
      this.criterionForPropertyAndSubquery( property,
                                            detachedCriteria,
                                            options )
    }
  }
  
  Criterion _dispatchToCriterion_(RelationalScope scope, AbstractPropertyExpression prop, options) {
    def property = prop.propertyFor(options)
    createCriterionForSubqueryReturning(scope, options) { detachedCriteria ->
      this.criterionForSubqueryAndProperty( detachedCriteria,
                                            property,
                                            options )
    }
  }
  
  Criterion _dispatchToCriterion_(ValueExpression val, RelationalScope scope, options) {
    createCriterionForSubqueryReturning(scope, options) { detachedCriteria ->
      this.criterionForValueAndSubquery( val.value,
                                         detachedCriteria,
                                         options )
    }
  }
  
  Criterion _dispatchToCriterion_(RelationalScope scope, ValueExpression val, options) {
    createCriterionForSubqueryReturning(scope, options) { detachedCriteria ->
      this.criterionForValueAndSubquery( detachedCriteria,
                                         val.value,
                                         options )
    }
  }
  
  Criterion _dispatchToCriterion_(ArithmeticExpression expr1, ArithmeticExpression expr2, options) {
    this.criterionForExpressionAndExpression(expr1, expr2, options)
  }
  
  Criterion _dispatchToCriterion_(ArithmeticExpression expr, ValueExpression val, options) {
    this.criterionForExpressionAndExpression(expr, val, options)
  }
  
  Criterion _dispatchToCriterion_(ValueExpression val1, ValueExpression val2, options) {
    this.criterionForExpressionAndExpression(val1, val2, options)
  }
  
  Criterion _dispatchToCriterion_(ValueExpression val, ArithmeticExpression expr, options) {
    this.criterionForExpressionAndExpression(val, expr, options)
  }
  
  Criterion _dispatchToCriterion_(ArithmeticExpression expr, AbstractPropertyExpression prop, options) {
    this.criterionForExpressionAndExpression(expr, prop, options)
  }
  
  Criterion _dispatchToCriterion_(AbstractPropertyExpression prop, ArithmeticExpression expr, options) {
    this.criterionForExpressionAndExpression(prop, expr, options)
  }
  
  Criterion _dispatchToCriterion_(AbstractPropertyExpression prop, ListExpression list, options) {
    if (list.containsOnlyPrimitives) {
      this.criterionForPropertyAndValue(prop.propertyFor(options), list.value*.value, options)
    } else {
      this.criterionForExpressionAndExpression(prop, list, options)
    }
  }
  
  
  // --------------------------------------------------------------------------
  // Other
  // --------------------------------------------------------------------------
  
  String inspect(indentationLevel=0, parent=null) {
    StringWriter writer = new StringWriter()
    writer.write(' ' * indentationLevel * 2)
    writer.write(lhsValue.inspect(indentationLevel + 1, this))
    writer.write(' ')
    writer.write(operatorInspectString)
    writer.write(': ')
    writer.write(rhsValue.inspect(indentationLevel + 1, this))
    writer.write('\n')
    
    writer.toString()
  }
  
  String toString() {
    return "ScopeComparisonBase[rhsValue: ${rhsValue}, lhsValue: ${lhsValue}]"
  }
  
}