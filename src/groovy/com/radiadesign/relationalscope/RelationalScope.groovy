package com.radiadesign.relationalscope

import org.hibernate.criterion.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class RelationalScope {
  
  static sessionFactory
  
  DefaultGrailsDomainClass grailsDomainClass
  
  String associationName
  def scopes = []
  def selections = []
  
  def skipCount
  def takeCount
  def orderBy = []
  
  def result
  def resultIsSet = false
  def results
  def resultsIsSet = false
  def resultCount
  def countIsSet = false
  
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
  
  protected RelationalScope( DefaultGrailsDomainClass _grailsDomainClass,
                             ArrayList _scopes, ArrayList _selections,
                             _takeCount, _skipCount, _orderBy) {
    // Provides a deep copy of the stored scopes to ensure thread safety
    scopes = _scopes.clone()
    grailsDomainClass = _grailsDomainClass
    selections = _selections
    takeCount = _takeCount
    skipCount = _skipCount
    orderBy = _orderBy
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

  def order(property, direction = 'asc') {
    def newScope = clone()
    newScope.orderBy.push([property: property, direction: direction])
    return newScope
  }
  
  def take(count) {
    def newScope = clone()
    newScope.takeCount = count
    return newScope
  }
  
  def skip(count) {
    def newScope = clone()
    newScope.skipCount = count
    return newScope
  }
  
  def all(forceRefresh=false) {
    if (!resultsIsSet || forceRefresh) { executeQuery(false) }
    return results
  }

  def count(forceRefresh=false) {
    if (resultsIsSet && !forceRefresh) {
      return results.size()
    }

    if (!countIsSet || forceRefresh) {
      executeCount()
    } 
    return resultCount
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

  def executableCriteria(unique) {
    def session = sessionFactory.currentSession
    def criteria = session.createCriteria(domainKlass, "root")
    prepareCriteria( criteria, [ criteria: criteria,
                                 currentRootAlias: "root",
                                 associationPath: associationName,
                                 associationAliases: [:],
                                 propertyMappings: [:],
                                 getDetachedCriteriaCount: { -> return detachedCriteriaCount },
                                 incrementDetachedCriteriaCount: { -> detachedCriteriaCount += 1 } ] )
    return criteria
  }
  
  def executeQuery(unique) {
    def criteria = executableCriteria(unique)
    if (unique) {
      resultIsSet = true
      result = criteria.uniqueResult()
    } else {
      resultsIsSet = true
      results = criteria.list()
    }
  }

  def executeCount() {
    countIsSet = true
    resultCount = executableCriteria(false).setProjection(Projections.rowCount()).list().first()
  }
  
  def prepareCriteria(criteria, options) {
    def criterion = this.toCriterion(options)
    
    if (criterion) {
      criteria.add(criterion)
    }
    
    def projection = toProjection()
    if (projection) {
      criteria.setProjection(projection)
    }
    
    if (takeCount != null) {
      criteria.maxResults = takeCount
    }
    
    if (skipCount != null) {
      criteria.firstResult = skipCount
    }

    orderBy.each { orderProperty ->
      def lowerDirection = orderProperty.direction.toLowerCase()
      if (!(lowerDirection.startsWith('asc') || lowerDirection.startsWith('desc'))) {
        throw RuntimeException("Order by direction '${orderProperty.direction}' is invalid. Use 'asc' or 'desc'.")
      }
      
      criteria.addOrder(new Order(orderProperty.property,
                                 lowerDirection.startsWith('asc')))
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
    
    def newOptions = options + [associationPath: currentAssociationPath]
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

  String toString() {
    "(${scopes.collect {it.toString()}.join(' && ')})"
  }
  
  // Provides a thread-safe copy of the current RelationalScope
  RelationalScope clone() {
    return this.class.newInstance(grailsDomainClass, scopes, selections, takeCount, skipCount, orderBy)
  }
  
}