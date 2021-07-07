(ns cc.journeyman.errata.html
  "Format back traces as HTML."
  (:require [cc.journeyman.errata.backtrace :refer [fold-backtrace]]
            [cc.journeyman.errata.rename :refer [recover-function-name
                                                 recover-namespace-name]]
            [clojure.java.io :refer [resource]]
            [clojure.string :refer [join starts-with?]]
            [hiccup.core :refer [html]]))

(defn map->table
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
   (map->table (dissoc frame :interesting? :frame))])


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

(defn html-backtrace
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
        (remove empty? (fold-backtrace error namespaces)))))]]))
