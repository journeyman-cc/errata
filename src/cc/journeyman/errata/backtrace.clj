(ns cc.journeyman.errata.backtrace
  "Extract useful backtrace information from Clojure exceptions."
  (:require [cc.journeyman.errata.registry :refer [interesting interesting?]]
            [cc.journeyman.errata.rename :refer [recover-function-name
                                                 recover-namespace-name]]
            [clojure.string :refer [join]]))

(defn construct-entry-for-frame
  "Construct an entry in a backtrace frame list for this `frame`, considering
these `namespaces` as interesting."
  [^StackTraceElement frame namespaces]
  (let [class-name (.getClassName frame)]
    {:interesting? (interesting? class-name namespaces)
     :class        class-name
     :frame        frame
     :file         (.getFileName frame)
     :function     (recover-function-name frame)
     :namespace    (recover-namespace-name frame)
     :line         (.getLineNumber frame)}))

(defn classify-backtrace
  "Produce a classified list of the backtrace for this `error`, considering 
these `namespaces` as interesting."
  [^Exception error namespaces]
  (when error
    (apply list
           (map
            (fn [frame]
              (construct-entry-for-frame frame namespaces))
            (.getStackTrace error)))))

(defn summarise-frame
  "Summarise a single stack `frame`, presented as a map."
  [frame]
  (join " " [(if (:function frame)
               (str
                (recover-namespace-name (:frame frame))
                "/"
                (recover-function-name (:frame frame)))
               (:class frame))
             "at line"
             (:line frame)
             "of file"
             (:file frame)]))

(defn fold-backtrace
  "Return the backtrace of this `error` as a list of lists, such that in each
sublist every member has the same value for `:interesting?` as derived from 
these `namspaces`."
  ([^Exception error namespaces]
   (loop [remainder      (classify-backtrace error namespaces)
          classification false
          accumulator    '()
          result         '()
          count          0]
;;;; Debugging! 
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
  ([^Exception error]
   (fold-backtrace error @interesting)))