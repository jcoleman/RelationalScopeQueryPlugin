require 'open3'

module Grails
  class Application
    attr_reader :root
    attr_accessor :domain_classes
    
    include InspectorController
    
    def initialize(directory)
      # We need something File-like
      @root = File.open(directory) unless directory.respond_to?(:path)
      
      # One day it'd be nice to parse existing classes
      @domain_classes = []
      @frozen_domain_classes = []
      
      @output = ""
    end

    def running?
      !!(@pipe)
    end
    
    def ready?
      return false unless running?
      
      Thread.critical = true
      ready = @output.include? "Server running."
      Thread.critical = false
      
      # update_output! unless ready
      return ready
    end
    
    def base_uri
      raise "Application not yet running." unless ready?
      return @output.scan(/^Server running\. Browse to (.+)/).first[0]
    end
    
    def add_domain_class(klass)
      @domain_classes << klass unless @domain_classes.include?(klass)
    end
    
    def needs_restart?
      !running? || domain_changes?
    end
    
    def domain_changes?
      domain_changes.size > 0
    end
    
    def domain_changes
      (domain_classes - @frozen_domain_classes)
    end
    
    def restart
      log "Performing a restart."
      
      stop! if running?
      run!
    end
    
    def run!
      log "Currently loaded domain classes: #{@frozen_domain_classes}"
      log "Need to load: #{domain_changes}"
      
      return unless domain_changes? || !running?
      
      # We need to dump all our _new or changed_ domain classes to actual files
      domain_changes.each do |domain|
        log "Writing #{domain.name} to disk..."
        domain.write_to @root
      end
      
      @frozen_domain_classes = domain_classes.clone
      
      install_inspector_controller_in(@root)
      
      Dir.chdir(@root.path)
      
      # Fancier:
      #@pipe, @pipe_out, @pipe_error = Open3.popen3("#{executable} run-app", "r")
      
      log "Starting Grails."
      @pipe = IO.popen("#{executable} run-app", "r")
      
      sleep(1)
      # If Grails hasn't said hello yet, something is wrong
      if @pipe.eof?
        raise Exception.new("Unable to start Grails. You may need to set your GRAILS_HOME environment variable.")
      end

      @output_watcher = Thread.new do
        Thread.current.abort_on_exception = true

        while(( line = @pipe.gets ))
          Thread.critical = true
          @output << line
          log "Grails: #{line}"
          Thread.critical = false
        end
      end
    end
    
    def executable
      ENV['GRAILS_HOME'] ? File.join(ENV['GRAILS_HOME'], "bin/grails") : `which grails`
    end
    
    def stop!
      if running?
        log "Terminating Grails..."
        
        @output_watcher.exit
        Process.kill("TERM", @pipe.pid)
        @pipe.close
        @output = ""
      
        @pipe = nil
      end
    end
    
    protected
    
    def update_output!
      @output << (@pipe.gets || "")
    end
    
    def log(message)
      puts "==> #{message}"
    end
  end
end