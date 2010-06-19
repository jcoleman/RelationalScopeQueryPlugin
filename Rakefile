require 'rubygems'
require 'rake'
require 'cucumber'
require 'cucumber/rake/task'
require 'drb'

require File.join( File.dirname(__FILE__), "features", "support", "grails", "grails" )

desc "Run the Cucumber tests."
Cucumber::Rake::Task.new(:features) do |t|
  t.cucumber_opts = "--format pretty"
end

desc "Start the Grails server in the background so you can run tests faster."
task :grails do

  # Create a GrailsApplication instance for the directory
  # where the tests are being run
  grails = Grails::Application.new(Dir.pwd)

  # start up the DRb service
  DRb.start_service "druby://localhost:10808", grails

  # We need the uri of the service to connect a client
  puts "Puppeteering at: #{DRb.uri}"

  # wait for the DRb service to finish before exiting
  begin
    DRb.thread.join
  rescue Interrupt
    grails.stop!
    grails.clean_files!
  end
end

task :default => ['features']
 
