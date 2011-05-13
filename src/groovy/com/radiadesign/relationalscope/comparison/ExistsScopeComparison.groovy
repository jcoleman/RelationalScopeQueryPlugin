package com.radiadesign.relationalscope.comparison

import org.hibernate.criterion.*
import com.radiadesign.relationalscope.RelationalScope

class ExistsScopeComparison extends ScopeComparisonBase {
  
  RelationalScope scope
  
  ExistsScopeComparison(RelationalScope _scope) {
    scope = _scope
  }
  
  def detachedCriteriaCallback(RelationalScope scope, criteria) {
    if (!scope.selections) {
      criteria.setProjection(Projections.id())
    }
  }
  
  Criterion toCriterion(options) {
    RelationalScope.addAssociationDescriptorToStack( options,
                                                     null,
                                                     scope.grailsDomainClass,
                                                     null )
    
    Criterion criterion = Subqueries.exists( detachedCriteriaFor(scope, options) )
    
    options.associationDescriptorStack.pop()
    
    return criterion
  }
  
  String toString() {
    return "(exists (${scope}))"
  }
  
}