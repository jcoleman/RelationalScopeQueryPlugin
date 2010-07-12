package com.radiadesign.relationalscope

import com.radiadesign.relationalscope.comparison.*
import com.radiadesign.relationalscope.expression.*

class RelationalScopeBuilder {
  
  RelationalScope _scope_
  
  def _scopeStack_
  
  RelationalScopeBuilder(RelationalScope scope) {
    _scope_ = scope
  }
  
  def methodMissing(String name, args) {
    assert args.size() == 1 : "Setting comparisons for a property may only be called with a single map"
    
    property(name)(args[0])
  }
  
  def _processArgs_(expression, argsMap) {
    def currentDomainClass = activeRelationalScope.grailsDomainClass
    if (expression instanceof LocalPropertyExpression && !(currentDomainClass.hasPersistentProperty(expression.propertyKey) || expression.propertyKey in ['id', 'version'])) {
      throw new RuntimeException("Domain class '${currentDomainClass.name}' does not contain a persistent property '${expression.propertyKey}'. Perhaps you have a typo in your relational query?" as String)
    }
    
    argsMap.each {
      if (it.key == 'where') {
        if (expression instanceof AbstractPropertyExpression) {
          _projectAssociation_( expression.propertyKey, _valueFor_(it.value) )
        } else {
          throw new RuntimeException("Only property expressions can use the 'where' comparator.")
        }
      } else {
        try {
          _addScopeOrComparisonToCurrentScope_(
            ScopeComparisonFactory."${it.key}"( expression, _valueFor_(it.value) )
          )
        } catch (MissingMethodException ex) {
          throw new RuntimeException("'${it.key}' is not a valid comparator. Perhaps you have a typo in your relational query?" as String)
        }
      }
    }
  }
  
  def _valueFor_(expression) {
    if ( expression instanceof ExpressionBase
         || expression instanceof RelationalScope
         || expression instanceof Closure ) {
      expression
    } else {
      value(expression)
    }
  }
  
  def where(Object[] args) {
    _project_( new RelationalScope(activeRelationalScope.grailsDomainClass), args )
  }
  
  def or(Object[] args) {
    _project_( new OrRelationalScope(activeRelationalScope.grailsDomainClass), args )
  }
  
  def and(Object[] args) {
    _project_( new RelationalScope(activeRelationalScope.grailsDomainClass), args )
  }
  
  def not(Object[] args) {
    _project_( new NotRelationalScope(activeRelationalScope.grailsDomainClass), args )
  }
  
  def exists(RelationalScope scope) {
    _addScopeOrComparisonToCurrentScope_(ScopeComparisonFactory.exists(scope))
  }
  
  def mapping(key) {
    new MappedPropertyExpression(key, this)
  }
  
  def property(key) {
    new LocalPropertyExpression(key, this)
  }
  
  def value(val) {
    new ValueExpression(val, this)
  }

  def getActiveRelationalScope() {
    _scopeStack_ && !_scopeStack_.empty() ? _scopeStack_.peek() : _scope_
  }
  
  def _addScopeOrComparisonToCurrentScope_(newScope) {
    activeRelationalScope.addScopeOrComparison(newScope)
  }
  
  def _project_(newScope, Object[] args) {
    if (!_scopeStack_) { _scopeStack_ = new Stack() }
    _scopeStack_.push(newScope)
    
    args.each { arg ->
      if (arg instanceof Closure) {
        arg.call()
      } else if (arg instanceof RelationalScope) {
        _addScopeOrComparisonToCurrentScope_(arg)
      } else {
        throw new IllegalArgumentException("Expecting instance of either Closure or RelationalScope; instead got: ${arg?.getClass()}")
      }
    }
    
    _addScopeOrComparisonToCurrentScope_( _scopeStack_.pop() )
  }
  
  def _projectAssociation_(associationName, Object[] args) {
    def currentDomainClass = activeRelationalScope.grailsDomainClass
    def domainProperty = currentDomainClass.getPropertyByName(associationName)
    if (!domainProperty.isAssociation()) {
      throw new RuntimeException("Domain class '${currentDomainClass.name}' property '${property}' is not an association and cannot be used with the 'where' comparator in a relational query?" as String)
    }
    
    def relation = new RelationalScope(domainProperty.getReferencedDomainClass())
    relation.associationName = associationName
    _project_(relation , args)
  }
  
  // Helper property for 'is' comparison
  final notNull = 1
  
  def getRelationalScope() {
    return _scope_
  }
  
}