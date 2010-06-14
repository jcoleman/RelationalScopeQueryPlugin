require 'net/http'

module Grails
  class Application
    module InspectorController
      def install_inspector_controller_in(root)
        name = File.join( root.path, "grails-app", "controllers", "InspectorController.groovy" )
        file = File.new(name, "w")
    
        file.write <<-EOD
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
        EOD
    
        file.close
      end
  
      def execute(code)
        until ready?
          sleep(0.1)
        end
        
        uri = URI.parse base_uri
        server = Net::HTTP.new(uri.host, uri.port)
        
        request = Net::HTTP::Post.new("#{base_uri}/inspector/execute")
        request.set_form_data({ :source => code })

        response = server.request(request)
        
        #puts "Response:\n\t#{response.body}"
        
        result = JSON.parse(response.body)
        raise "Execution failed:\n#{result['error']}" if result['error']
        
        return result['result']
      end
    end
  end
end