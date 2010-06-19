if ENV.keys.find {|env_var| env_var.match(/^TM_/)}
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
end