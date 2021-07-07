(ns cc.journeyman.errata.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [cc.journeyman.errata.core :refer [serr summarise-error]]))

(defmacro non-empty-string?
  [s]
  `(and (string? ~s) (pos? (count ~s))))

(deftest construct-entry-test
  (testing "Error summary."
    (let [err (try
                (/ 1 0)
                (catch Exception e e))]
      (is (empty? (with-out-str (summarise-error err [])))
          "If nothing is interesting, nothing should be printed.")
      (is (empty? (with-out-str (serr err [])))
          "Shortcut should behave exactly the same as the full name")
      (is (empty? (with-out-str (summarise-error err ["punch.and.judy"])))
          "If what is interesting isn't in the trace, nothing should be printed.")
      (is (empty? (with-out-str (serr err ["punch.and.judy"])))
          "Shortcut should behave exactly the same as the full name")
      (is (non-empty-string? (with-out-str (summarise-error err ["clojure.lang.Number"])))
          "If something in the trace is interesting, something should be printed.")
      (is (non-empty-string? (with-out-str (serr err ["clojure.lang.Number"])))
          "Shortcut should behave exactly the same as the full name"))))
