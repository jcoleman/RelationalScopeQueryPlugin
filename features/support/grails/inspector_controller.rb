require 'net/http'
require 'json'
require 'fileutils'

module Grails
  class Application
    module InspectorController
      def install_inspector_controller_in(root)
        location = File.join( root.path, "grails-app", "controllers" )
        FileUtils.mkdir_p(location)
        
        name = File.join(location, "InspectorController.groovy")
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
                render([error: e.message, stackTrace: e.stackTrace.collect { it.toString() }] as JSON)
              }

            }
          }
        EOD
    
        file.close
      end
  
      def execute(code)
        restart if needs_restart?
        until ready?
          sleep(0.1)
        end
        
        uri = URI.parse base_uri
        server = Net::HTTP.new(uri.host, uri.port)
        
        log "Executing code: \n#{code}\n"
        
        request = Net::HTTP::Post.new("#{base_uri}/inspector/execute")
        request.set_form_data({ :source => code })

        response = server.request(request)
        
        log "Response:\n\t#{response.body}\n"
        
        result = JSON.parse(response.body)
        
        if result['error']
          cleaned_stack = result['stackTrace'].find_all do |line|
            line =~ /com.radia/
          end.join("\n")
          
          raise "Execution failed:\n#{result['error']}\n#{cleaned_stack}"
        end
        
        return result['result']
      end
    end
  end
end