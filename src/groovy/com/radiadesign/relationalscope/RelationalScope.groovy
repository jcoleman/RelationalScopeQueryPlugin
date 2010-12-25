package com.radiadesign.relationalscope

import org.hibernate.criterion.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class RelationalScope {
  
  static sessionFactory
  
  DefaultGrailsDomainClass grailsDomainClass
  
  String associationName
  def scopes = []
  def selections = []
  def selectionKeys = []
  
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
  
  protected RelationalScope(RelationalScope scope) {
    // Provides a deep copy of the stored scopes to ensure thread safety
    scopes = scope.scopes.clone()
    grailsDomainClass = scope.grailsDomainClass
    selections = scope.selections.clone()
    selectionKeys = scope.selectionKeys.clone()
    takeCount = scope.takeCount
    skipCount = scope.skipCount
    orderBy = scope.orderBy.clone()
  }
  
  
  // --------------------------------------------------------------------------
  // Public API
  // --------------------------------------------------------------------------
  
  def where(RelationalScope additionalScope) {
    def newScope = this.clone()
    newScope.addScopeOrComparison(additionalScope)
    return newScope
  }
  
  def where(ArrayList additionalScopes) {
    def newScope = this.clone()
    additionalScopes.each { additionalScope ->
      if (!additionalScope instanceof RelationalScope) {
        throw new RuntimeException("scope.where(ArrayList) expects each item to be an instance of RelationalScope. Instead found ${additionalScope?.getClass()}")
      }
      
      newScope.addScopeOrComparison(additionalScope)
    }
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
  
  def select(ArrayList _selections, ArrayList _selectionKeys=[]) {
    def newScope = this.clone()
    newScope.selections += _selections
    newScope.selectionKeys += _selectionKeys
    return newScope
  }
  
  def select(Closure block) {
    if (selectionKeys.size() > 0) {
      throw new RuntimeException("Invalid use of scope.select(Closure) after use of scope.select(Map)")
    }
    
    def builder = new SelectionBuilder()
    block = block.clone()
    block.delegate = builder
    block.resolveStrategy = Closure.DELEGATE_FIRST
    block.call()
    return this.select(builder._selections_)
  }
  
  // param _selections is a map with each entry in one the following formats:
  //   <result-key-name>: "propertyName"
  //   <result-key-name>: "association.propertyName"
  //   <result-key-name>: { selection builder } // e.g. { min("propertyName") }
  def select(Map _selections) {
    if (selectionKeys.size() != selections.size()) {
      throw new RuntimeException("Invalid use of scope.select(Map) after use of scope.select(Closure)")
    }
    
    def _selectionKeys = []
    def builder = new SelectionBuilder()
    _selections.each { kv ->
      if (kv.value instanceof Closure) {
        def block = kv.value.clone()
        block.delegate = builder
        block.resolveStrategy = Closure.DELEGATE_FIRST
        block.call()
      } else if (kv.value instanceof String || kv.value instanceof GString) {
        builder.property(kv.value)
      } else {
        throw new RuntimeException("Invalid use of scope.select(Map): each key-value pair's value must be a Closure, String, or GString")
      }
      
      _selectionKeys << kv.key
    }
    
    return this.select(builder._selections_, _selectionKeys)
  }

  def order(property, direction = 'asc') {
    direction = direction.toLowerCase()
    if (!(direction.startsWith('asc') || direction.startsWith('desc'))) {
      throw new RuntimeException("Order by direction '${direction}' is invalid. Use 'asc' or 'desc'.")
    }
    
    def newScope = clone()
    newScope.orderBy << [property: property, direction: direction]
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
    if (this.class == RelationalScope && this.class == additionalScope.class) {
      scopes += additionalScope.scopes
    } else {
      scopes << additionalScope
    }
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
  
  def mapifySelectionList(fields) {
    def map = [:]
    int len = selectionKeys.size()
    for (int i = 0; i < len; ++i) {
      map[selectionKeys[i]] = fields[i]
    }
    return map
  }
  
  def mapifyResultsIfNecessary(results) {
    if (selectionKeys.isEmpty()) {
      return results
    } else {
      def maps = new ArrayList(results.size())
      if (selectionKeys.size() > 1) {
        for (result in results) {
          maps << mapifySelectionList(result)
        }
      } else {
        for (result in results) {
          maps << mapifySelectionList([result])
        }
      }
      return maps
    }
  }
  
  def mapifyResultIfNecessary(result) {
    if (selectionKeys.isEmpty()) {
      return result
    } else {
      if (selectionKeys.size() > 1) {
        return mapifySelectionList(result)
      } else {
        return mapifySelectionList([result])
      }
    }
  }
  
  def executeQuery(unique) {
    def criteria = executableCriteria(unique)
    if (unique) {
      resultIsSet = true
      result = mapifyResultIfNecessary( criteria.uniqueResult() )
    } else {
      resultsIsSet = true
      results = mapifyResultsIfNecessary( criteria.list() )
    }
  }

  def executeCount() {
    countIsSet = true
    resultCount = executableCriteria(false).setProjection(Projections.countDistinct("id")).list().first()
  }
  
  def prepareCriteria(criteria, options) {
    def criterion = this.toCriterion(options)
    
    if (criterion) {
      criteria.add(criterion)
    }
    
    def projection = toProjection(options)
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
      criteria.addOrder( new Order(orderProperty.property,
                                   orderProperty.direction == 'asc') )
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
  
  static createAssociationAliasIfNecessary(options, associationPath) {
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
  
  Criterion toCriterion(options) {
    def currentAssociationPath = fullAssociationPath(options.associationPath)
    
    // Do we need to create new alias?
    if (associationName) {
      RelationalScope.createAssociationAliasIfNecessary(options, currentAssociationPath)
    }
    
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
  
  Projection toProjection(options) {
    if (selections.empty) {
      return null
    } else {
      if (selections.size() == 1) {
        return selections.first().toProjection(options)
      } else {
        return selections.inject(Projections.projectionList()) { projection, selection ->
          projection.add(selection.toProjection(options))
        }
      }
    }
  }

  String toString() {
    "(${scopes.collect {it.toString()}.join(' && ')})"
  }
  
  // Provides a thread-safe copy of the current RelationalScope
  RelationalScope clone() {
    return this.class.newInstance(this)
  }
  
}