(ns cc.journeyman.errata.nrepl
  "Middleware to allow errata integration into `nrepl`, q.v."
  (:require 
   [errata.core :refer [summarise-error show-html-backtrace]]
   [nrepl.middleware :as middleware :refer [set-descriptor!]]))
  
  
  (defn wrap-errata
    [])
  
  ;; NOTE: At this stage, this is simply a copy of the example at https://nrepl.org/nrepl/building_middleware.html
  (set-descriptor! #'wrap-errata
                   {:requires #{}
                    :expects #{}
                    :handles {"evaluation"
                              {:doc "Provides better backtrace debugging."
                               :requires {"form" "The form to evaluate."}
                               :optional {"ns" "The namespace in which we want to obtain completion candidates. Defaults to `*ns*`."
                                          "complete-fn" "The fully qualified name of a completion function to use instead of the default one (e.g. `my.ns/completion`)."
                                          "options" "A map of options supported by the completion function."}
                               :returns {"value" "The value evaluated. As a side effect, may print or show in browser backtrace information if evaluation fails."}}}})