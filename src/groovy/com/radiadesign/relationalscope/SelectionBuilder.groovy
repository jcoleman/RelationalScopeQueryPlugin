package com.radiadesign.relationalscope

import com.radiadesign.relationalscope.selection.*

class SelectionBuilder {
  
  def selections = []
  
  def methodMissing(String name, args) {
    assert args.empty : "Currently property selections do not support any arguments"
    
    property(name)
  }
  
  def property(String propertyName) {
    selections << new PropertySelection(propertyName)
  }
  
  def id() {
    selections << new IdentifierSelection()
  }
  
  def max(String propertyName) {
    selections << new MaximumSelection(propertyName)
  }
  
  def min(String propertyName) {
    selections << new MinimumSelection(propertyName)
  }
  
  def sum(String propertyName) {
    selections << new SummationSelection(propertyName)
  }
  
  def average(String propertyName) {
    selections << new AverageSelection(propertyName)
  }
  
  def count(String propertyName) {
    selections << new CountSelection(propertyName)
  }
  
  def count(DistinctSelection property) {
    selections << new CountSelection(property)
  }
  
  def distinct(String propertyName) {
    selections << new DistinctSelection(property(propertyName))
  }
  
  def distinct(AbstractSelection property) {
    selections << new DistinctSelection(property)
  }
  
}