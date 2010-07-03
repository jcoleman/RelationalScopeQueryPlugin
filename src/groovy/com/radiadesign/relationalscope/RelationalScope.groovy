package com.radiadesign.relationalscope

import org.hibernate.criterion.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class RelationalScope {
  
  static sessionFactory
  
  DefaultGrailsDomainClass grailsDomainClass
  
  String associationName
  def scopes = []
  def selections = []
  
  def result
  def resultIsSet = false
  def results
  def resultsIsSet = false
  
  def detachedCriteriaCount = 0
  
  // --------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------
  
  RelationalScope(DefaultGrailsDomainClass _grailsDomainClass) {
    grailsDomainClass = _grailsDomainClass
  }
  
  //RelationalScope(JSONObject json) {
  //  
  //}
  
  protected RelationalScope(DefaultGrailsDomainClass _grailsDomainClass, ArrayList _scopes) {
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
  
  def select(ArrayList _selections) {
    def newScope = this.clone()
    newScope.selections += _selections
    return newScope
  }
  
  def select(Closure block) {
    def builder = new SelectionBuilder()
    block = block.clone()
    block.delegate = builder
    block.resolveStrategy = Closure.DELEGATE_FIRST
    block.call()
    return this.select(builder.selections)
  }
  
  def all(forceRefresh=false) {
    if (!resultsIsSet || forceRefresh) { executeQuery(false) }
    return results
  }
  
  def first(forceRefresh=false) {
    if (all(forceRefresh).size() > 0) {
      return all().first()
    } else {
      return null
    }
  }
  
  def find(forceRefresh=false) {
    if (!resultIsSet || forceRefresh) { executeQuery(true) }
    return result
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
  
  def executeQuery(unique) {
    def session = sessionFactory.currentSession
    def criteria = session.createCriteria(domainKlass, "root")
    def criterion = this.toCriterion( [ criteria: criteria,
                                        currentRootAlias: "root",
                                        associationName: associationName,
                                        associationAliases: [:],
                                        propertyMappings: [:],
                                        getDetachedCriteriaCount: { -> return detachedCriteriaCount },
                                        incrementDetachedCriteriaCount: { -> detachedCriteriaCount += 1 } ] )
    criteria.add(criterion)
    
    def projection = toProjection()
    if (projection) {
      criteria.setProjection(projection)
    }
    
    if (unique) {
      resultIsSet = true
      result = criteria.uniqueResult()
    } else {
      resultsIsSet = true
      results = criteria.list()
    }
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
  
  static aliasDiscriminatorFor(options) {
    options.isDetachedCriteria ? "sqjn_${options.getDetachedCriteriaCount()}" : 'rtjn'
  }
  
  def createAssociationAliasIfNecessary(options, associationPath) {
    if (associationName) {
      def discriminator = RelationalScope.aliasDiscriminatorFor(options)
      def aliasMap = options.associationAliases[discriminator]
      if (!aliasMap) {
        aliasMap = options.associationAliases[discriminator] = [:]
      }
      if (!aliasMap[associationPath]) {
        def alias = "${discriminator}_${associationPath.replace('.', '_')}"
        options.criteria.createAlias(associationPath, alias, CriteriaSpecification.LEFT_JOIN)
        aliasMap[associationPath] = alias
      }
    }
  }
  
  Criterion toCriterion(options) {
    def currentAssociationPath = fullAssociationPath(options.associationPath)
    
    // Do we need to create new alias?
    createAssociationAliasIfNecessary(options, currentAssociationPath)
    
    def newOptions = options + [associationName: currentAssociationPath]
    if (scopes.size() == 1) {
      // If there is only one scope contained in this object then
      // just delegate to that scope.
      return scopes.first().toCriterion(newOptions)
    } else if (scopes.size() > 1) {
      // The default combination strategy of multiple scopes is AND.
      scopes.inject(this.junction()) { criterion, scope ->
        def additionalCriterion = scope.toCriterion(newOptions)
        if (additionalCriterion) { criterion.add(additionalCriterion) }
        return criterion
      }
    } else {
      // There is no criteria to create.
      return null
    }
  }
  
  Projection toProjection() {
    if (selections.empty) {
      return null
    } else {
      if (selections.size() == 1) {
        return selections.first().toProjection()
      } else {
        return selections.inject(Projections.projectionList()) { projection, selection ->
          projection.add(selection.toProjection())
        }
      }
    }
  }
  
  // Provides a thread-safe copy of the current RelationalScope
  RelationalScope clone() {
    return this.class.newInstance(grailsDomainClass, scopes)
  }
  
}