module Grails
  class DomainClass
    attr_reader :source
    attr_reader :unique_hash
    
    def initialize(source)
      @source = source
      # Force our name to be parsed so we fail early
      self.name
    end
    
    def name
      unless @name
        # Parse the name from the source code
        @name = @source.scan(/[^\{]*class ([A-Z]\w*)/).first.first
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
    
    def ==(other)
      hash == other.hash
    end
    
    def eql?(other)
      self == other
    end
    
    # Calculate a unique hash based on the name and contents —
    # this allows us to no-op the creation of the same domain class
    def hash
      (self.name + @source).hash
    end
  end
end