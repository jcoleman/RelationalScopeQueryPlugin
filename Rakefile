require 'rubygems'
require 'rake'
require 'cucumber'
require 'cucumber/rake/task'

desc "Run the Cucumber tests."
Cucumber::Rake::Task.new(:features) do |t|
  t.cucumber_opts = "--format pretty"
end

task :default => ['features']
 
