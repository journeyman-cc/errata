(ns cc.journeyman.errata.rename-test
  (:require [clojure.string :refer [ends-with?]]
            [clojure.test :refer [deftest testing is]]
            [cc.journeyman.errata.rename :refer [fn-name recover-function-name]]))

;; Example class names which appear in backtraces:
;; "clojure.lang.Numbers"
;; "nrepl.middleware.interruptible_eval$evaluate"
;; "clojure.main$repl$fn__9095"
;; "clojure.main$repl"
;; "clojure.main$repl$read_eval_print__9086$fn__9089"
;; "clojure.core$apply"
;; "clojure.lang.RestFn"
;; "nrepl.middleware.interruptible_eval$evaluate$fn__1468$fn__1469"
;; "cc.journeyman.errata.core$fn__2538"
;; "nrepl.middleware.interruptible_eval$interruptible_eval$fn__1499$fn__1503"
;; "clojure.lang.AFn"
;; "nrepl.middleware.session$session_exec$main_loop__1566"
;; "clojure.lang.Compiler"
;; "clojure.core$with_bindings_STAR_"
;; "nrepl.middleware.session$session_exec$main_loop__1566$fn__1570"
;; "clojure.lang.Compiler$DefExpr"
;; "clojure.lang.Compiler$InvokeExpr"
;; "nrepl.middleware.interruptible_eval$evaluate$fn__1468"
;; "clojure.core$eval"
;; "java.lang.Thread"
;; "clojure.main$repl$read_eval_print__9086"

(deftest function-name-test
  (testing "extraction of Clojure function names from class names reported in backtrace frames"
    (let [class-name "clojure.main$repl"
          expected "repl"
          actual (fn-name class-name)]
      (is (= actual expected)
          "Where the class is a Clojure function, extract its name."))
    (let [class-name "clojure.lang.Numbers"
          expected nil
          actual (fn-name class-name)]
      (is (= actual expected)
          "Where the class isn't a Clojure function, don't extract a name."))
    (let [class-name "nrepl.middleware.interruptible_eval$evaluate$fn__1468$fn__1469"
          expected "evaluate"
          actual (fn-name class-name)]
      (is (= actual expected)
          "Where the class is an anonymous function nested within a named Clojure function, extract the name of the named function."))
    (let [err (try
                (/ 1 0)
                (catch Exception e e))
          clj (filter
               #(ends-with? (.getFileName %) ".clj")
               (.getStackTrace err))
          java (filter
                #(ends-with? (.getFileName %) ".java")
                (.getStackTrace err))]
      (is (every? string? (map recover-function-name clj))
          "Every frame whose source languate is clojure ought to have a function name")
      (is (every? nil? (map recover-function-name java))
          "No frame whose source language is Java ought to have a function name")
      (is (every? #(pos? (count (recover-function-name %))) clj)
          "No function name should be the empty string.")
      ;; TODO: ought to do a regex test on the recovered function names.
      ;; TODO: don't believe this regex captures all the valid names!
      (is (every? #(re-find #"^[a-z0-9-?!+-/*]*$" %) (map recover-function-name clj)))
      )))