Given /^I have the following domain class:$/ do |code|
  @grails.stop! if @grails.running?
  @grails.domain_classes << Grails::DomainClass.new(code)
end

Given /^I have created the following "([^\"]*)" instances:$/ do |klass, instances|
  @grails.run! unless @grails.running?
  
  instances.hashes.each do |instance|
    # If the instance has a parent reference, it needs to be
    # called out. As Ryan says… make some grand assumptions.
    extra_properties = ""
    if instance.has_key? "parent"
      extra_properties << <<-EOF
      instance.parent = #{klass}.findByName("#{instance["parent"]}")
      EOF
      instance.delete("parent")
    end
    
    # Lamely convert the Hash into a Groovy Map
    groovy_map = instance.to_json.gsub("{", "[").gsub("}", "]")
    
    # Execute code to create and save an instance of the given class
    # with the appropriate properties. The obtuse newInstance
    # syntax is so we can resolve the class name correctly.
    code = <<-EOD
      def instance = #{klass}.newInstance(#{groovy_map})
      #{extra_properties}
      instance.save()
    EOD
    @grails.execute code
  end
  
  # Mark this class as having data that needs to be deleted
  @dirty_classes << klass
end

When /^I execute the code "([^\"]*)"$/ do |code|
  @result = @grails.execute code
end

When /^I execute the following code:$/ do |code|
  @result = @grails.execute code
end

Then /^I should get the following results:$/ do |instances|
  # We only care about certain columns
  columns = instances.raw.first.inspect
  
  # Remove columns we don't care about from the results hashes
  thinned_result = @result.collect do |instance|
    instance.reject { |key, value| !columns.include? key }
  end
  
  thinned_result.should == instances.hashes
end
