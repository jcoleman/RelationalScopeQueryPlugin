package com.radiadesign.relationalscope

import org.hibernate.criterion.*
import org.hibernate.FetchMode
import com.radiadesign.relationalscope.comparison.InScopeComparison
import com.radiadesign.relationalscope.comparison.ExistsScopeComparison
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class RelationalScope {
  
  static sessionFactory
  
  DefaultGrailsDomainClass grailsDomainClass
  
  // This property being set indicates that we're walking an association
  // in the traditional "joining" sense.
  String associationName
  // This property being set indicates that we're walking an association
  // in a non-traditional has-many subquery way.
  String virtualAssociationName
  
  def scopes = []
  def selections = []
  def selectionKeys = []
  
  def fetchedAssociations = []
  
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
  
  protected RelationalScope(RelationalScope scope) {
    // Provides a deep copy of the stored scopes to ensure thread safety
    scopes = scope.scopes.clone()
    grailsDomainClass = scope.grailsDomainClass
    selections = scope.selections.clone()
    selectionKeys = scope.selectionKeys.clone()
    fetchedAssociations = scope.fetchedAssociations.clone()
    takeCount = scope.takeCount
    skipCount = scope.skipCount
    orderBy = scope.orderBy.clone()
    
    // The following isn't necessary because scopes only get cloned at the parent level,
    // i.e. where no association exists. (Technically they do get cloned when entering
    // a has-many sub-query...but we manually set what we want of these properties
    // in that code.)
    // - associationName = scope.associationName
    // - virtualAssociationName = scope.virtualAssociationName
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
    def builder
    def newScope = this.clone()
    
    additionalScopes.each { additionalScope ->
      if (additionalScope instanceof RelationalScope) {
        newScope.addScopeOrComparison(additionalScope)
      } else if (additionalScope instanceof Closure) {
        if (!builder) { builder = new RelationalScopeBuilder(instance()) }
        
        additionalScope = additionalScope.clone()
        additionalScope.delegate = builder
        additionalScope.resolveStrategy = Closure.DELEGATE_FIRST
        additionalScope.call()
      } else {
        throw new RuntimeException("scope.where(ArrayList) expects each item to be an instance of RelationalScope or Closure. Instead found ${additionalScope?.getClass()}")
      }
    }
    
    if (builder) {
      newScope.addScopeOrComparison(builder.relationalScope)
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
  
  def fetch(Object[] associations) {
    def newScope = this.clone()
    // Without the cast, instead of [] we get [[]]
    newScope.fetchedAssociations += (Collection)associations
    return newScope
  }
  
  def fetch(Collection associations) {
    def newScope = this.clone()
    newScope.fetchedAssociations += associations
    return newScope
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
    if ( this.class == RelationalScope
         && this.class == additionalScope.class
         && this.associationName == additionalScope.associationName ) {
      scopes += additionalScope.scopes
    } else {
      scopes << additionalScope
    }
  }
  
  Class getDomainKlass() {
    return grailsDomainClass?.clazz
  }
  
  static contrainedPropertiesForDomain(domain) {
    domain.getConstrainedProperties()
  }
  
  static associationIsNullable(domain, propertyName) {
    contrainedPropertiesForDomain(domain)[propertyName].nullable
  }
  
  
  // --------------------------------------------------------------------------
  // Private API
  // --------------------------------------------------------------------------

  def executableCriteria(unique, options=null) {
    def session = sessionFactory.currentSession
    def criteria = session.createCriteria(domainKlass, "root")
    
    def newOptions = [ criteria: criteria,
                       rootDomainClass: grailsDomainClass,
                       currentRootAlias: "root",
                       associationPath: associationName,
                       associationDescriptorStack: new Stack(),
                       associationAliases: [:],
                       propertyMappings: [:],
                       getDetachedCriteriaCount: { -> return detachedCriteriaCount },
                       decrementDetachedCriteriaCount: { -> detachedCriteriaCount -= 1 },
                       incrementDetachedCriteriaCount: { -> detachedCriteriaCount += 1 } ]
    if (null != options) { newOptions.putAll(options) }
    
    prepareCriteria(criteria, newOptions)
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
    if (selectionKeys.isEmpty() || result == null) {
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
    resultCount = executableCriteria(false, [ignoreOrderBy: true])
                    .setProjection(Projections.countDistinct("id"))
                    .list()
                    .first()
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
    
    if (!options.ignoreOrderBy) {
      orderBy.each { orderProperty ->
        def prop = RelationalScope.propertyFor(options, orderProperty.property)
        criteria.addOrder( new Order(prop, orderProperty.direction == 'asc') )
      }
    }
    
    fetchedAssociations.each { association ->
      criteria.setFetchMode(association, FetchMode.JOIN)
    }
  }
  
  static junctionInspectString = 'and'
  
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
  
  static aliasDiscriminatorFor(options, associationDescriptor=null) {
    if (associationDescriptor != null) {
      associationDescriptor.isDetachedCriteria ? "sqjn_${associationDescriptor.detachedCriteriaCount}" : 'rtjn'
    } else {
      options.isDetachedCriteria ? "sqjn_${options.getDetachedCriteriaCount()}" : 'rtjn'
    }
  }
  
  static createAssociationAliasIfNecessary(options, associationPath, domain, propertyName, associationDescriptor=null) {
    def discriminator = RelationalScope.aliasDiscriminatorFor(options, associationDescriptor)
    def optionsOrAssociationDescriptor = (associationDescriptor ?: options)
    def aliasMap = optionsOrAssociationDescriptor.associationAliases[discriminator]
    if (!aliasMap) {
      aliasMap = optionsOrAssociationDescriptor.associationAliases[discriminator] = [:]
    }
    if (!aliasMap[associationPath]) {
      def joinSpecification = associationIsNullable(domain, propertyName) ? CriteriaSpecification.LEFT_JOIN : CriteriaSpecification.INNER_JOIN
      def alias = "${discriminator}_${associationPath.replace('.', '_')}"
      optionsOrAssociationDescriptor.criteria
                                    .createAlias( associationPath,
                                                  alias,
                                                  joinSpecification )
      aliasMap[associationPath] = alias
    }
  }
  
  static retrieveAndValidateSelectableProperty(domainClass, associationName) {
    def domainProperty = domainClass.getPropertyByName(associationName)
    if (!domainProperty.isAssociation()) {
      throw new RuntimeException("Domain class '${domainClass.name}' property '${associationName}' is not an association, but you attempted to walk over it when choosing a property.")
    }
    if (domainProperty.isOneToMany()) {
      throw new RuntimeException("You attempted to use the one-to-many association ${associationName} when choosing a selectable property. This is not allowed.")
    }
    return domainProperty
  }
  
  static propertyFor(options, propertyKey, overrideAssociationDescriptor=null) {
    def alias
    def associationPath
    def associationDescriptor = overrideAssociationDescriptor
    
    if (associationDescriptor == null && !options.associationDescriptorStack.empty()) {
      associationDescriptor = options.associationDescriptorStack.peek()
    }
    
    // Determine if an association...
    def lastDotIndex = propertyKey.lastIndexOf('.')
    if (lastDotIndex != -1) {
      if (lastDotIndex == 0 || lastDotIndex == propertyKey.size() - 1) {
        throw new RuntimeException("Selected property string cannot start or end with a dot, was: '${propertyKey}'")
      }
      
      associationPath = propertyKey[0..lastDotIndex-1]
      propertyKey = propertyKey[lastDotIndex+1..-1]
    }
    
    def domainClass
    if (associationPath) {
      def associations = associationPath.tokenize('.')
      def path = associations[0]
      
      if (associationDescriptor?.parentDomainClass) {
        domainClass = associationDescriptor.parentDomainClass
      } else {
        domainClass = options.rootDomainClass
      }
      
      def currentAssociationProperty = RelationalScope.retrieveAndValidateSelectableProperty(domainClass, path)
      
      RelationalScope.createAssociationAliasIfNecessary(options, path, domainClass, currentAssociationProperty.name, associationDescriptor)
      
      if (associations.size() > 1) {
        associations[1..-1].each { association ->
          currentAssociationProperty = RelationalScope.retrieveAndValidateSelectableProperty(
                                         currentAssociationProperty.referencedDomainClass,
                                         association
                                       )
          
          path += ".${association}"
          RelationalScope.createAssociationAliasIfNecessary(options, path, currentAssociationProperty.domainClass, currentAssociationProperty.name, associationDescriptor)
        }
      }
      
      def discriminator = RelationalScope.aliasDiscriminatorFor(options, associationDescriptor)
      def discriminatedAliases = (associationDescriptor ?: options).associationAliases[discriminator]
      assert discriminatedAliases : "An association was used for which no alias has been created"
      alias = discriminatedAliases[associationPath]
      assert alias : "An association was used for which no alias has been created"
    } else if (overrideAssociationDescriptor != null) {
      alias = overrideAssociationDescriptor.currentRootAlias
    }
    
    return "${alias ?: options.currentRootAlias}.${propertyKey}"
  }
  
  static addAssociationDescriptorToStack(options, path, parentDomainClass, associationDomainProperty) {
    options.associationDescriptorStack.push(
      path: path,
      parentDomainClass: parentDomainClass,
      associationDomainProperty: associationDomainProperty,
      isDetachedCriteria: options.isDetachedCriteria,
      detachedCriteriaCount: options.getDetachedCriteriaCount(),
      associationAliases: options.associationAliases,
      currentRootAlias: options.currentRootAlias,
      criteria: options.criteria
    )
  }
  
  Criterion toCriterion(options) {
    def currentAssociationPath = fullAssociationPath(options.associationPath)
    def associationDescriptorStack = options.associationDescriptorStack
    
    def association = associationName ?: virtualAssociationName
    def parentDomainClass = grailsDomainClass
    def addedPathToStack = ( associationDescriptorStack.empty() || association )
    if (addedPathToStack) {
      // Track our association path and the associated grails domain property.
      def associationDomainProperty
      if (association) {
        if (!associationDescriptorStack.empty()) {
          parentDomainClass = associationDescriptorStack.peek().parentDomainClass
        }
        associationDomainProperty = parentDomainClass.getPropertyByName(association)
      }
      
      RelationalScope.addAssociationDescriptorToStack(
        options,
        currentAssociationPath,
        associationDomainProperty ? associationDomainProperty.referencedDomainClass : parentDomainClass,
        associationDomainProperty
      )
    }
    
    // Are we digging into an associaton? Specifically a one-to-one association...
    if (associationName) {
      // If so, do we need to create new alias?
      RelationalScope.createAssociationAliasIfNecessary(options, currentAssociationPath, parentDomainClass, associationName)
    }
    
    Criterion criterion
    def newOptions = options + [associationPath: currentAssociationPath]
    if (scopes.size() == 1) {
      // If there is only one scope contained in this object then
      // just delegate to that scope.
      criterion = scopes.first().toCriterion(newOptions)
    } else if (scopes.size() > 1) {
      // The default combination strategy of multiple scopes is AND.
      criterion = scopes.inject(this.junction()) { criterionAccumulator, scope ->
        def additionalCriterion = scope.toCriterion(newOptions)
        if (additionalCriterion) { criterionAccumulator.add(additionalCriterion) }
        return criterionAccumulator
      }
    } else {
      // There is no criteria to create.
      criterion = null
    }
    
    if (addedPathToStack) {
      associationDescriptorStack.pop()
    }
    return criterion
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
  
  String inspect(indentationLevel=0, parent=null) {
    StringWriter writer = new StringWriter()
    def wroteParen = false
    if (null == parent) {
      writer.write(' ' * indentationLevel * 2)
      writer.write(grailsDomainClass.name)
      writer.write('.where {\n')
      wroteParen = true
    } else if (null != associationName) {
      writer.write(' ' * indentationLevel * 2)
      writer.write('property(')
      writer.write(associationName.inspect())
      writer.write(') where: {\n')
      wroteParen = true
    } else if (parent instanceof InScopeComparison || parent instanceof ExistsScopeComparison) {
      writer.write(grailsDomainClass.name)
      writer.write('.where {\n')
      wroteParen = true
    }
    
    if (scopes.size() == 1) {
      writer.write( scopes.first().inspect(indentationLevel + 1, this) )
    } else if (scopes.size() > 1) {
      writer.write(' ' * indentationLevel * 2)
      writer.write(this.class.junctionInspectString)
      writer.write(' {\n')
      scopes.each { scope ->
        writer.write( scope.inspect(indentationLevel + 1, this) )
      }
      writer.write(' ' * indentationLevel * 2)
      writer.write('}\n')
    } else {
      writer.write(' ' * indentationLevel * 2)
      writer.write('/* Empty Scope */')
    }
    
    if (wroteParen) {
      writer.write(' ' * indentationLevel * 2)
      writer.write('}\n')
    }
    
    writer.toString()
  }

  String toString() {
    "(${scopes.collect {it.toString()}.join(' && ')})"
  }
  
  // Provides a thread-safe copy of the current RelationalScope
  RelationalScope clone() {
    return this.class.newInstance(this)
  }
  
}