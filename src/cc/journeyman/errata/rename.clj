(ns cc.journeyman.errata.rename
  "Recover the original Clojure function and namespace names from information 
in backtrace frames, by reversing the lexical substitutions by which they're 
rendered into valid Java names"
  (:require [clojure.string :refer [ends-with? join replace split starts-with?]]))

(defn- dot-name
  [^String munged]
  (let [parts (split munged #"\$")]
    (replace
     (first (remove #(starts-with? % "fn__") parts))
     "_"
     "-")))

(defn- fn-name
  [^String munged]
  (last (split (dot-name munged) #"\.")))

(defn- ns-name
  [^String munged]
  (join "." (butlast (split (dot-name munged) #"\."))))

(defn recover-function-name
  [^StackTraceElement frame]
  (when (ends-with? (.getFileName frame) ".clj")
    (fn-name (.getClassName frame))
    ))

(defn recover-namespace-name
  [^StackTraceElement frame]
  (when (ends-with? (.getFileName frame) ".clj")
    (ns-name (.getClassName frame))))