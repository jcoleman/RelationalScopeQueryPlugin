          import grails.converters.*

          class InspectorController {
            def execute = {
              def source = """def propertyMissing(String name) {
                def clazz = grailsApplication.allClasses.find { it.name == name }
                return clazz ?: super.propertyMissing(name)
              }

              $params.source"""
              try {
                def result = Eval.me("grailsApplication", grailsApplication, source)
                render([result: result] as JSON)
              } catch (e) {
                render([error: e.message] as JSON)
              }

            }
          }
