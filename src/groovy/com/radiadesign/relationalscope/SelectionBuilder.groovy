package com.radiadesign.relationalscope

import com.radiadesign.relationalscope.selection.*

class SelectionBuilder {
  
  def _selections_ = []
  
  def methodMissing(String name, args) {
    assert args.size() == 0 : "Currently property selections do not support any arguments; got ${args}"
    
    property(name)
  }
  
  def property(String propertyName) {
    _addSelection_( new PropertySelection(propertyName) )
  }
  
  def id() {
    _addSelection_( new IdentifierSelection() )
  }
  
  def max(String propertyName) {
    _addSelection_( new MaximumSelection(propertyName) )
  }
  
  def min(String propertyName) {
    _addSelection_( new MinimumSelection(propertyName) )
  }
  
  def sum(String propertyName) {
    _addSelection_( new SummationSelection(propertyName) )
  }

  def groupBy(String propertyName) {
    _addSelection_( new GroupingSelection(propertyName) )
  }
  
  def average(String propertyName) {
    _addSelection_( new AverageSelection(propertyName) )
  }
  
  def count(String propertyName) {
    _addSelection_( new CountSelection(propertyName) )
  }
  
  def count(DistinctSelection property) {
    _addSelection_( new CountSelection(property) )
  }
  
  def distinct(String propertyName) {
    _addSelection_( new DistinctSelection(property(propertyName)) )
  }
  
  def distinct(AbstractSelection property) {
    _addSelection_( new DistinctSelection(property) )
  }
  
  def _addSelection_(AbstractSelection selection) {
    if ( _selections_.size() > 0 && _selections_[-1].is(selection.property) ) {
      // If the "new" selection is just wrapping a previous one, don't select both separately
      _selections_[-1] = selection
    } else {
      _selections_ << selection
    }
  }
  
}