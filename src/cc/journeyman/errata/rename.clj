(ns cc.journeyman.errata.rename
  "Recover the original Clojure function and namespace names from information 
in backtrace frames, by reversing the lexical substitutions by which they're 
rendered into valid Java names"
  (:require [clojure.string :refer [ends-with? join replace split starts-with?]]))

(defn remove-anon
  "Remove anonomous function elements from a `munged` name."
  [^String munged]
  (remove #(starts-with? % "fn__") 
          (split munged #"[\.\$]")))

(defn fn-name
  "De-mung a `munged` function name"
  [^String munged]
  (replace (last (remove-anon munged)) "_" "-"))

(defn namespace-name
  "De-mung a `munged` namspace name"
  [^String munged]
  (replace (join "." (butlast (remove-anon munged))) "_" "-"))

(defn recover-function-name
  [^StackTraceElement frame]
  (when (ends-with? (.getFileName frame) ".clj")
    (fn-name (.getClassName frame))))

(defn recover-namespace-name
  [^StackTraceElement frame]
  (when (ends-with? (.getFileName frame) ".clj")
    (namespace-name (.getClassName frame))))