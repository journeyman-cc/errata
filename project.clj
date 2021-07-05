(defproject errata "0.1.0-SNAPSHOT"
  :description "A library designed to filter out from Clojure backtraces those 
frames likely to be of interest to the developer"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[hiccup "1.0.5"]
                 [org.clojure/clojure "1.10.1"]]
  :repl-options {:init-ns cc.journeyman.errata.core})
