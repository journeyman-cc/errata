(ns cc.journeyman.errata.registry
  "A registry for registering interest in namespaces."
  (:require [clojure.set :refer [difference]]
            [clojure.string :refer [starts-with?]]))

(def interesting
  "The set of namespaces considered interesting."
  (atom #{}))

(defn interesting!
  "Declare a `namespace` as interesting"
  [^String namespace]
  (swap! interesting conj namespace))

(defn uninteresting!
  "Declare a `namespace` not to be interesting"
[^String namespace]
  (when
   (@interesting namespace)
    (swap! interesting difference #{namespace})))

(def interesting?
  "`true` if this `name` starts with one of these `prefixes`, else `false`."
  (memoize
   (fn ([name prefixes]
        (or (some #(starts-with? name %) prefixes) false))
     ([name]
      (or
       (@interesting name)
       (interesting? name @interesting))))))
