(defproject cc.journeyman/errata "0.1.0"
  :cloverage {:output "docs/cloverage"
              :codecov? true}
  :codox {:metadata {:doc "FIXME: write docs"
                     :doc/format :markdown}
          :output-path "docs"}
  :dependencies [[hiccup "1.0.5"]
                 [org.clojure/clojure "1.10.1"]]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :description "A library designed to filter out from Clojure backtraces those 
frames likely to be of interest to the developer"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :plugins [[lein-cloverage "1.2.2"]
            [lein-codox "0.10.7"]]
  ;; NOTE WELL: `lein release` won't work until we have a release repository
  ;; set, which we don't!
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "v." "--no-sign"]
                  ["clean"]
                  ["codox"]
                  ["cloverage"]
                  ["uberjar"]
                  ;; ["release"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]]
  :repl-options {:init-ns cc.journeyman.errata.core}
  :scm {:name "git" :url "https://github.com/journeyman-cc/errata"}
  :url "https://journeyman-cc.github.io/errata/")
