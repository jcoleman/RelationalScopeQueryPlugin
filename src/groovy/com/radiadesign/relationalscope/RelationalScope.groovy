package com.radiadesign.relationalscope

import org.hibernate.criterion.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class RelationalScope {
  
  static sessionFactory
  
  DefaultGrailsDomainClass grailsDomainClass
  
  String associationName
  def scopes = []
  
  def results
  
  // --------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------
  
  RelationalScope(DefaultGrailsDomainClass _grailsDomainClass) {
    grailsDomainClass = _grailsDomainClass
  }
  
  //RelationalScope(JSONObject json) {
  //  
  //}
  
  private RelationalScope(DefaultGrailsDomainClass _grailsDomainClass, ArrayList _scopes) {
    // Provides a deep copy of the stored scopes to ensure thread safety
    scopes = _scopes.clone()
    grailsDomainClass = _grailsDomainClass
  }
  
  
  // --------------------------------------------------------------------------
  // Public API
  // --------------------------------------------------------------------------
  
  def where(RelationalScope additionalScope) {
    def newScope = this.clone()
    newScope.addScopeOrComparison(additionalScope)
    return newScope
  }
  
  def where(Closure block) {
    def builder = new RelationalScopeBuilder(instance())
    block = block.clone()
    block.delegate = builder
    block.resolveStrategy = Closure.DELEGATE_FIRST
    block.call()
    return this.where(block.relationalScope)
  }
  
  
  // --------------------------------------------------------------------------
  // Protected API (can be used by this package, should not be used externally)
  // --------------------------------------------------------------------------
  
  def addScopeOrComparison(additionalScope) {
    scopes << additionalScope
  }
  
  Class getDomainKlass() {
    return grailsDomainClass?.clazz
  }
  
  
  // --------------------------------------------------------------------------
  // Private API
  // --------------------------------------------------------------------------
  
  def executeQuery() {
    def session = sessionFactory.currentSession
    def criteria = session.createCriteria(domainKlass)
    def criterion = this.toCriterion(criteria, associationName, [:])
    criteria.add(criterion)
    results = criteria.list()
  }
  
  def junction() {
    Restrictions.conjunction()
  }
  
  def instance() {
    new RelationalScope(grailsDomainClass)
  }
  
  def fullAssociationPath(associationPath) {
    if (associationName) {
      return associationPath ? "${associationPath}.${associationName}" : associationName
    } else {
      return associationPath
    }
  }
  
  Criterion toCriterion(criteria, associationPath, associationAliases) {
    def currentAssociationPath = fullAssociationPath(associationPath)
    
    // Do we need to create new alias?
    if (associationName && !associationAliases[currentAssociationPath]) {
      def alias = currentAssociationPath.replace('.', '_')
      criteria.createAlias(currentAssociationPath, alias, CriteriaSpecification.LEFT_JOIN)
      associationAliases[currentAssociationPath] = alias
    }
    
    if (scopes.size() == 1) {
      // If there is only one scope contained in this object then
      // just delegate to that scope.
      return scopes.first().toCriterion(criteria, currentAssociationPath, associationAliases)
    } else if (scopes.size() > 1) {
      // The default combination strategy of multiple scopes is AND.
      scopes.inject(this.junction()) { criterion, scope ->
        criterion.add(scope.toCriterion(criteria, currentAssociationPath, associationAliases))
      }
    } else {
      // There is no criteria to create.
      return null
    }
  }
  
  // Provides a thread-safe copy of the current RelationalScope
  RelationalScope clone() {
    return new RelationalScope(grailsDomainClass, scopes)
  }
  
}