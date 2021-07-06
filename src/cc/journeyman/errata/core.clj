(ns cc.journeyman.errata.core
  "Generate more useful backtraces."
  (:require [cc.journeyman.errata.back-trace :refer [classify-back-trace summarise-frame]]
            [cc.journeyman.errata.html :refer [html-back-trace]]
            [cc.journeyman.errata.registry :refer [interesting interesting!]]
            [clojure.java.browse :refer [browse-url]])
  (:import [java.io File]))

(def err
  "An example exception for development use, will be removed before a release
 is made."
  (try
    (/ 1 0)
    (catch Exception e e)))

(defn show-interesting-trace
  "Show the interesting details of this `error`, considering these `namespaces`
to be interesting, or the registered interests if no `namespaces` argument 
is passed."
  ([^Exception error namespaces]
   (doall 
    (map 
     println 
     (map 
      summarise-frame 
      (filter :interesting? (classify-back-trace error namespaces)))))
   nil)
  ([^Exception error]
   (show-interesting-trace error @interesting)))

(defn register-interesting-ns!
  "Declare a `namespace` as interesting."
  ;; This is essentially just a convenience function so that users don't have
  ;; to explicitly load the registry namespace.
  [namespace]
  (interesting! namespace))

(defn show-html-back-trace
  "Show the back trace for this `error` as HTML folded to focus on these
interesting `namespaces` (or the registered interests if no `namespaces` argument
is passed), in a browser window."
  ([^Exception error namespaces]
   (let [file (File/createTempFile "backtrace" ".html")]
     (spit file (html-back-trace error namespaces))
     (browse-url (str "file:" (.getAbsolutePath file)))))
  ([^Exception error]
   (show-html-back-trace error @interesting)))

;; to demonstrate what this does, evaluate

;; (show-html-back-trace err ["nrepl.middleware"])