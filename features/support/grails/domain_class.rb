module Grails
  class DomainClass
    attr_reader :source
    
    def initialize(source)
      @source = source
      # Force our name to be parsed so we fail early
      self.name
    end
    
    def name
      unless @name
        # Parse the name from the source code
        @name = @source.scan(/[^\{]*class ([A-Z]\w*)/).first
        raise "Unable to parse class name from class:\n#{@source}" unless @name
      end
      @name
    end
    
    # Write the code for this domain to a file, in preparation for 
    # running the application.
    def write_to(root)
      path = File.join(root.path, "grails-app", "domain", "#{name}.groovy")
      file = File.new( path, "w")
      
      file.write(@source)
      file.close
    end
  end
end