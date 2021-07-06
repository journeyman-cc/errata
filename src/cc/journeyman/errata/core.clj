(ns cc.journeyman.errata.core
  "Generate more useful backtraces."
  (:require [cc.journeyman.errata.rename :refer [recover-function-name
                                                 recover-namespace-name]]
            [clojure.java.browse :refer [browse-url]]
            [clojure.java.io :refer [resource]]
            [clojure.string :refer [join starts-with?]]
            [hiccup.core :refer [html]])
  (:import [java.io File]))

(def err
  "An example exception for development use, will be removed before a release
 is made."
  (try
    (/ 1 0)
    (catch Exception e e)))

(def is-interesting?
  "`true` if this `name` starts with one of these `prefixes`, else `false`."
  (memoize
   (fn [name prefixes]
     (or (some #(starts-with? name %) prefixes) false))))

(defn construct-entry-for-frame
  "Construct an entry in a back-trace frame list for this `frame`, considering
these `namespaces` as interesting."
  [^StackTraceElement frame namespaces]
  (let [class-name (.getClassName frame)]
    {:interesting? (is-interesting? class-name namespaces)
     :class        class-name
     :frame        frame
     :file         (.getFileName frame)
     :function     (recover-function-name frame)
     :namespace    (recover-namespace-name frame)
     :line         (.getLineNumber frame)}))

(defn classify-back-trace
  "Produce a classified list of the backtrace for this `error`, considering 
these `namespaces` as interesting."
  [^Exception error namespaces]
  (apply list
         (map
          (fn [frame]
            (construct-entry-for-frame frame namespaces))
          (.getStackTrace error))))


(defn fold-back-trace
  "Return the backtrace of this `error` as a list of lists, such thet in each
sublist every member has the same value for `:interesting?` as derived from 
these `namspaces`."
  [^Exception error namespaces]
  (loop [remainder      (classify-back-trace error namespaces)
         classification false
         accumulator    '()
         result         '()
         count          0]
;;    (print (str "Remaining: " (count remainder)
;;                "; classification: " classification
;;                "; accumulator: " (count accumulator)
;;                "; result: " (count result) "\n"))
    (let [current (assoc (first remainder) :index count)]
      (if (empty? remainder)
        (if (empty? accumulator)
          (reverse result)
          (reverse (cons (reverse accumulator) result)))
        (if (= classification (:interesting? current))
          (recur (rest remainder)
                 classification
                 (cons current accumulator)
                 result
                 (inc count))
          (recur (rest remainder)
                 (:interesting? current)
                 (list current)
                 (cons (reverse accumulator) result)
                 (inc count)))))))

(defn map-to-table
  "Format this map `m` as an HTML table."
  [m]
  [:table
   (map #(vector :tr [:th %] [:td (m %)])
        (keys m))])

(defn html-frame
  "format a single stack `frame`, presented as a map, as HTML."
  [frame]
  [:div {:class "frame"}
   [:div {:class "headline"}
    [:strong (if (:function frame)
               (str
                (recover-namespace-name (:frame frame))
                "/"
                (recover-function-name (:frame frame)))
               (:class frame))]
    (join
     " "
     [" at line"
      (:line frame)
      "of file"
      (:file frame)])]
   (map-to-table (dissoc frame :interesting? :frame))])


(defn- html-group
  "Format a `group` of stack frames as HTML."
  [group]
  (let [interest (if (:interesting? (first group))
                   "interesting"
                   "noise")]
    [:div {:class (str "container-" interest)}
     [:h5 (str (count group) " " interest " frames:")]
     (apply
      vector
      (concat [:div {:class interest}]
              (map
               #(html-frame %)
               group)))]))

(defn html-back-trace
  "Format the back trace for this `error` as HTML, folded to focus on these
interesting `namespaces`."
  [^Exception error namespaces]
  (html
   [:html
    [:head
     [:title "Backtrace"]
     [:style
      ;; TODO: slurp this from a resource
      (slurp (resource "style.css"))]]
    [:body
     (apply
      vector
      (concat
       [:div {:class "backtrace"}]
       (map
        html-group
        (remove empty? (fold-back-trace error namespaces)))))]]))

(defn show-html-back-trace
  "Show the back trace for this `error` as HTML folded to focus on these
interesting `namespaces`, in a browser window."
  [^Exception error namespaces]
  (let [file (File/createTempFile "backtrace" ".html")]
    (spit file (html-back-trace error namespaces))
    (browse-url (str "file:" (.getAbsolutePath file)))))

;; to demonstrate what this does, evaluate

;; (show-html-back-trace err ["nrepl.middleware"])