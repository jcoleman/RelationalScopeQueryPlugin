require "rubygems"
require "json"
require "spec"
require "set"
require "drb"
require "pp"

require File.join( File.dirname(__FILE__), "grails/grails" )

# Create a GrailsApplication instance for the directory
# where the tests are being run
grails = nil
run_locally = false
if run_locally
  grails = Grails::Application.new(Dir.pwd)
else
  DRb.start_service

  # attach to the DRb server via a URI given on the command line
  grails = DRbObject.new nil, "druby://localhost:10808"
end

module Kernel
  alias_method :old_puts, :puts
  def puts(text)
    old_puts "<pre>#{text}</pre>"
  end
  
  alias_method :old_pp, :pp
  def pp(object)
    old_puts "<pre>"
    old_pp object
    old_puts "</pre>"
  end
end

class String
  def first_letter_capitalize
    self.chars.inject("") { |m, c| m << (m.empty? ? c.upcase : c) }
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
  grails.stop! if grails.running? && run_locally
end