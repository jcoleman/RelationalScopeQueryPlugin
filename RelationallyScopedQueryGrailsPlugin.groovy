import com.radiadesign.relationalscope.RelationalScope

class RelationallyScopedQueryGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [hibernate:"1.2.0 > *"]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/controllers/InspectorController.groovy"
    ]
    def observes = ["domain"]

    // TODO Fill in these fields
    def author = "James Coleman"
    def authorEmail = "jtc331+scopedquery@gmail.com"
    def title = "Relationally Scoped Querying"
    def description = '''\\
This plugin was inspired by both the power of Groovy builders in HibernateCriteriaBuilder 
and the new Relation querying API that appeared in ActiveRecord with Rails 3.0. Major goals 
include arbitrarily nested reuasable scopes and lazy execution of the query generated. Also available 
will be a level of introspection into the logical expression generated that Criteria cannot provide.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/relationally-scoped-query"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { context ->
      application.domainClasses.each { domainClass ->
        installStaticMethods(domainClass)
      }
    }
    
    def installStaticMethods(domainClass) {
      Class klass = domainClass.clazz
      klass.metaClass.static.blankScope = { ->
        return new RelationalScope(domainClass)
      }
      
      // Only install the defaultScope if the user has not already provided one
      if (!klass.metaClass.getStaticMetaMethod("defaultScope")) {
        klass.metaClass.static.defaultScope = { ->
          return klass.blankScope()
        }
      }
      
      klass.metaClass.static.where = { arg ->
        klass.defaultScope().where(arg)
      }
      
      klass.metaClass.static.getAt = { index ->
        klass.where {
          id equals: index as Long
        }.find()
      }
    }

    def doWithApplicationContext = { applicationContext ->
        RelationalScope.sessionFactory = applicationContext.getBean("sessionFactory")
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
        application.domainClasses.each { domainClass ->
          installStaticMethods(domainClass)
        }
        RelationalScope.sessionFactory = event.ctx.sessionFactory
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
