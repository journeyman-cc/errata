(ns cc.journeyman.errata.backtrace-test
  (:require [clojure.test :refer [deftest testing is]]
            [cc.journeyman.errata.backtrace :refer [construct-entry-for-frame]]
            [cc.journeyman.errata.rename :refer [namespace-name]]))

(deftest construct-entry-test
  (testing "Construction of maps wrapping StackTraceElement instances."
    (let [err (try
                (/ 1 0)
                (catch Exception e e))]
      (let [expected true
            actual (map?
                    (construct-entry-for-frame
                     (first (.getStackTrace err)) []))]
        (is (= actual expected) "Result should be a map."))
      (let [frame (first (.getStackTrace err))
            entry (construct-entry-for-frame frame [])]
        (let [expected true
              actual (instance?
                      StackTraceElement
                      (:frame entry))]
          (is (= actual expected)
              "Frame should be cached on the :frame key"))
        (let [expected true
              actual (= frame (:frame entry))]
          (is (= actual expected)
              "And, obviously, it should be the same frame."))
        (let [expected false
              actual (:interesting? entry)]
          (is (= actual expected)
              "As we haven't passed and namespaces, no frame will be interesting."))
        ;; OK, it's not going to be easy to do reliable tests on
        ;; namespaces when we don't actually know the execution
        ;; environment, but...
        (let [ns-name (namespace-name (:class entry))
              e2 (construct-entry-for-frame (:frame entry) [ns-name])
              expected true
              actual (:interesting? e2)]
          (is (= actual expected)
              "If the namespace passed is the namespace of the class of the frame, it is interesting."))
        (let [expected true
              actual (integer? (:line entry))]
          (is (= actual expected)
              "Every entry should have a line value, which should be an integer."))
        (let [f2 (nth (.getStackTrace err) 1)
              e2 (construct-entry-for-frame f2 [])
              expected false]
          (let [actual (= entry e2)]
            (is (= actual expected)
                "The entries for different frames should be different"))
          (let [actual (= (:frame entry) (:frame e2))]
            (is (= actual expected)
                "And their frame values should be different")))))))


