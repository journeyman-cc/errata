(ns errata.core
  (:require [clojure.string :refer [starts-with?]]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def err (try
           (/ 1 0)
           (catch Exception e e)))

(def is-interesting?
  (memoize
   (fn [name prefixes]
     (some #(starts-with? name %) prefixes))))

(defn construct-entry-for-frame
  [^StackTraceElement frame namespaces]
  (let [class-name (.getClassName frame)]
     {:interesting? (is-interesting? class-name namespaces)
      :class class-name
      :file  (.getFileName frame)
      :line  (.getLineNumber frame)}))

(defn classify-back-trace
  [^Exception error & namespaces]
  (apply list
         (map
          (fn [frame]
            (construct-entry-for-frame frame namespaces))
          (.getStackTrace error))))

(defn get-back-trace-as-list [^Exception error]
  (apply list (map
               (fn [frame] 
                 {:class (.getClassName frame)
                  :file  (.getFileName frame)
                  :line (.getLineNumber frame)})
               (.getStackTrace error))))

