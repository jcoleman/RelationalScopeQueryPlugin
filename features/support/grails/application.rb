# Fancier: require 'open3'

module Grails
  class Application
    attr_reader :root
    attr_accessor :domain_classes
    
    include InspectorController
    
    def initialize(directory)
      # We need something File-like
      @root = File.open(directory) unless directory.respond_to?(:path)
      
      # One day it'd be nice to load existing classes
      @domain_classes = []
      
      @output = ""
    end
    
    def running?
      !!(@pipe)
    end
    
    def ready?
      return false unless running?
      
      ready = @output.include? "Server running."
      update_output! unless ready
      return ready
    end
    
    def base_uri
      raise "Application not yet running." unless ready?
      return @output.scan(/^Server running\. Browse to (.+)/).first[0]
    end
    
    def run!
      # We need to dump all our domain classes to actual files
      domain_classes.each do |domain|
        domain.write_to @root
      end
      
      install_inspector_controller_in(@root)
      
      Dir.chdir(@root.path)
      
      # Fancier:
      # @pipe, @pipe_out, @pipe_error = Open3.popen3("#{grails_executable} run-app", "r")
      
      @pipe = IO.popen("#{executable} run-app", "r")
      
      sleep(1)
      # If Grails hasn't said hello yet, something is wrong
      if @pipe.eof?
        raise Exception.new("Unable to start Grails. You may need to set your GRAILS_HOME environment variable.")
      end
    end
    
    def executable
      ENV['GRAILS_HOME'] ? File.join(ENV['GRAILS_HOME'], "bin/grails") : `which grails`
    end
    
    def stop!
      Process.kill("TERM", @pipe.pid)
      @pipe.close
      @output = ""
      
      @pipe = nil
    end
    
    protected
    
    def update_output!
      @output << (@pipe.gets || "")
    end
  end
end