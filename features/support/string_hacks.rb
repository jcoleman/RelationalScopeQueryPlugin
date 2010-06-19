class String
  def first_letter_capitalize
    self.chars.inject("") { |m, c| m << (m.empty? ? c.upcase : c) }
  end
end