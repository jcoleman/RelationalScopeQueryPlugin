Given /^I have the following domain class:$/ do |code|
  @grails.add_domain_class Grails::DomainClass.new(code)
end

def create_instances_from_hashes(klass, hashes)
  instances = hashes.collect do |instance|
    create_instance_from_hash(klass, instance)
  end
  
  instances
end

def create_instance_from_hash(klass, hash)
  # Lamely convert the Hash into a Groovy Map
  groovy_map = hash.to_json.gsub("{", "[").gsub("}", "]")
  
  # Execute code to create and save an instance of the given class
  # with the appropriate properties. The obtuse newInstance
  # syntax is so we can resolve the class name correctly.
  code = <<-EOD
    def instance = #{klass}.newInstance(#{groovy_map})
    instance.save()
    instance
  EOD
  
  # Mark this class as having data that needs to be deleted
  @dirty_classes << klass
  
  @grails.execute code
end

Given /^I have created the following "([^\"]*)" instances:$/ do |klass, instances|
  create_instances_from_hashes klass, instances.hashes
end

Given /^I have created the following "([^\"]*)" graph:$/ do |klass, graph|
  instances = JSON.parse(graph)
  instances.each do |instance|
    # Find all the properties of this instance that are themselves
    # hashes. These represent relationships.
    references_relationship_keys = instance.keys.find_all do |key|
      instance[key].respond_to? :keys
    end
    
    has_many_relationship_keys = instance.keys.find_all do |key|
      instance[key].respond_to? :at
    end
    
    # Now, pull the values of these relationships out
    # of the original instances. We'll link everything up later.
    relationship_transformer = lambda do |key|
      # We want relationships that include the property name, like this:
      target = (instance.delete key)
      target_class = target.delete "class"
      identifier = target.first
      { :property => key,
        :property_class => target_class,
        :identifying_property => identifier[0],
        :identifying_value => identifier[1] }
    end
    
    references_relationships = references_relationship_keys.collect relationship_transformer
    has_many_relationships = 
    has_many_relationship_keys.inject do |collection, key|
      instance[key].each do |rel|
        collection << relationship_transformer.call(rel)
      end
      collection
    end
    
    # Actually create the object
    instance[:actual] = create_instance_from_hash klass, instance
    
    # Now that it's created, we can add back the relationship key
    instance[:references_relationships] = references_relationships
    instance[:references_relationships] = references_relationships
  end
  
  # Now, go back and create all the relationships using
  # what Ryan calls "grand assumptions."
  instances.each do |instance|
    actual = instance[:actual]
    instance[:relationships].each do |relationship|
      @grails.execute <<-EOF
        def instance = #{klass}.get(#{actual["id"]})
        instance.#{relationship[:property]} = #{relationship[:property_class]}.findBy#{relationship[:identifying_property].first_letter_capitalize}(#{relationship[:identifying_value].inspect})
        instance.save()
      EOF
    end
  end
end

When /^I execute the code "([^\"]*)"$/ do |code|
  @result = @grails.execute code
end

When /^I execute the following code:$/ do |code|
  @result = @grails.execute code
end

Then /^I should get the following results(, in order)?:$/ do |order, instances|
  # We only care about certain columns
  columns = instances.raw.first.inspect
  
  # Remove columns we don't care about from the results hashes
  thinned_result = @result.collect do |instance|
    stringified_instance = Hash.new
    instance.reject { |key, value| !columns.include? key }.each do |key, value|
      stringified_instance[key] = value ? value.to_s : value
    end
    stringified_instance
  end
  
  #puts "results"
  #thinned_result.each do |i|
  #  i.each do |k,v|
  #    puts k ? k.bytes.to_a.inspect : k
  #    puts v ? v.bytes.to_a.inspect : v
  #  end
  #end
  #
  #puts "instances"
  #instances.hashes.each do |i|
  #  i.each do |k,v|
  #    puts k ? k.bytes.to_a.inspect : k
  #    puts v ? v.bytes.to_a.inspect : v
  #  end
  #end
  
  thinned_result.size.should == instances.hashes.size
  Set.new(thinned_result).to_a.should include(*Set.new(instances.hashes).to_a)
end
