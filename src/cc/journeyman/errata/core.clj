(ns cc.journeyman.errata.core
  "Generate more useful backtraces."
  (:require [cc.journeyman.errata.backtrace :refer [classify-backtrace summarise-frame]]
            [cc.journeyman.errata.html :refer [html-backtrace]]
            [cc.journeyman.errata.registry :refer [interesting interesting!]]
            [clojure.java.browse :refer [browse-url]])
  (:import [java.io File]))

(defn register-interesting-ns!
  "Declare a `namespace` as interesting."
  ;; This is essentially just a convenience function so that users don't have
  ;; to explicitly load the registry namespace.
  [namespace]
  (interesting! namespace))

(defn summarise-error
  "Show the interesting details of this `error`, considering these `namespaces`
to be interesting, or the registered interests if no `namespaces` argument 
is passed."
  ([^Exception error namespaces]
   (doall 
    (map 
     println 
     (map 
      summarise-frame 
      (filter :interesting? (classify-backtrace error namespaces)))))
   nil)
  ([^Exception error]
   (summarise-error error @interesting))
  ([] (when *e (summarise-error *e))))

(def serr 
  "Convenience shortcut for `summarise-error`"
  summarise-error)

(defn show-html-backtrace
  "Show the back trace for this `error` as HTML folded to focus on these
interesting `namespaces` (or the registered interests if no `namespaces` argument
is passed), in a browser window."
  ([^Exception error namespaces]
   (let [file (File/createTempFile "backtrace" ".html")]
     (spit file (html-backtrace error namespaces))
     (browse-url (str "file:" (.getAbsolutePath file)))))
  ([^Exception error]
   (show-html-backtrace error @interesting))
  ([] (when *e (show-html-backtrace *e))))

;; to demonstrate what this does, evaluate

;; (show-html-backtrace err ["nrepl.middleware"])