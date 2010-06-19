require "rubygems"
require "json"
require "spec"
require "set"
require "drb"
require "pp"

["pre_for_textmate",
 "string_hacks"].each do |file|
   require File.join( File.dirname(__FILE__), file )
end

require File.join( File.dirname(__FILE__), "grails/grails" )

# Create a GrailsApplication instance for the directory
# where the tests are being run
grails = nil
run_locally = false
unless run_locally
  DRb.start_service

  # attach to the DRb server via a URI given on the command line
  grails = DRbObject.new nil, "druby://localhost:10808"
  
  begin
    grails.executable
  rescue DRb::DRbConnError => e
    # Fall back to regular one-Grails-per-test-run mode
    run_locally = true
    grails = Grails::Application.new(Dir.pwd)
  end
end

raise "You need to specify a GRAILS_HOME in order to run the tests." unless grails.executable && File.exist?(grails.executable)

Before do
  @grails = grails
  @dirty_classes = Set.new
end

After do |scenario| 
  # Purge all the dirty classes
  @dirty_classes.each do |klass|
    grails.execute <<-EOD
      #{klass}.list().each { it.delete() }
    EOD
  end
end

at_exit do
  grails.shutdown! if grails.running? && run_locally
end