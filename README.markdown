Relationally Scoped Query Plugin
================================

This plugin was inspired by both the power of Groovy builders in HibernateCriteriaBuilder and the new Relation querying API that appeared in ActiveRecord with Rails 3.0.

Major goals include arbitrarily nested reusable scopes and lazy execution of the query generated. Also available will be a level of introspection into the logical expression generated that Criteria cannot provide.

Examples
========

See the `features/` directory for examples of usage.

More complex features:

    
    TestDomain.where { id equals: 1 as Long }

    TestDomain.where {
     // 't' is a single relationship to another TestDomain
     t.where {
       id gte: 1 as Long
     }
    }

    TestDomain.where {
     where(anotherRelationalScopeObject) // the object is returned by a where({})
    }

    TestDomain.where {
     id mapTo: "parent_query_id"
     t.where {
       id equals: mapping("parent_query_id")
     }
    }

    TestDomain.where {
     or {
       // same thing in each comparison :)
       id equals: property("id")
       property("id") equals: property("id")
     }
    }
