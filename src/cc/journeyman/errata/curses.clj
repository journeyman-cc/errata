(ns cc.journeyman.errata.curses
  "Like `summarise-error`, but with curses."
  (:require [cc.journeyman.errata.backtrace :refer [classify-backtrace
                                                    summarise-frame]]
            [cc.journeyman.errata.registry :refer [interesting]]
            [lanterna.screen :refer [get-key-blocking get-screen put-string
                                     redraw start stop]]))

(defn cursed-errata
  "An experimental replacement for `summarise-error`, q.v., using curses."
  ;; TODO: not yet doing what I want, because I want it to print into the
  ;; *same* window, rather than opening a new one.
  ;; TODO: this does not yet produce a scrolling window if the number of
  ;; 'interesting' frames won't fit in the default window.
  ([^Exception error namespaces]
   (let [trace (map
                summarise-frame
                (filter :interesting? (classify-backtrace error namespaces)))
         scr (get-screen)]
     (when error
       (do
         (start scr)
         (put-string
          scr 0 1 (str (.getName (.getClass error)) ": " (.getMessage error)))
         (doall
          (map #(put-string scr 0 (+ %2 2) %1 {:fg :yellow :bg :red})
               trace
               (range)))
         (put-string scr 0 (+ (count trace) 3) "Press any key to return to REPL")
         (redraw scr)
         (get-key-blocking scr)
         (stop scr)))))
  ([^Exception error]
   (cursed-errata error @interesting))
  ([]
   (cursed-errata *e @interesting)))
