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
    createCriterionForSubqueryReturning(scope, options) { detachedCriteria ->
      Subqueries.exists(detachedCriteria)
    }
  }
  
  String toString() {
    return "(exists (${scope}))"
  }
  
  String inspect(indentationLevel=0, parent=null) {
    StringWriter writer = new StringWriter()
    writer.write('' * indentationLevel * 2)
    writer.write('exists {\n')
    writer.write(scope.inspect(indentationLevel + 1, this))
    writer.write('}\n')
    
    writer.toString()
  }
  
}