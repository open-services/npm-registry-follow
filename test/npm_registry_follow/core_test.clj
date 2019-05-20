(ns npm-registry-follow.core-test
  (:require [clojure.test :refer :all]
            [npm-registry-follow.core :refer :all]))

(def expected-changes 3)

(defn callback-test [func-under-test expect-errors?]
  (let [changes (atom [])
        done (promise)]
    (def stop (func-under-test
                (fn [id]
                  (swap! changes conj id)
                  (println "got item")
                  (when expect-errors?
                    (is (true? (:error? id)))
                    (is (instance? Exception (:exception id))))
                  (when (> (count @changes) (- expected-changes 1))
                    (is (= expected-changes (count @changes)))
                    ;; all items are unique
                    (is (= (count (set @changes)) (count @changes)))
                    (deliver done true)
                    (stop)))))
    (println "Waiting for test to finish...")
    @done))

(deftest listen-for-changes-test
  (testing "can read a list of package changes"
    (callback-test listen-for-changes false)))

(deftest poll-for-changes-test
  (testing "can read a list of package changes"
    (callback-test (partial poll-for-changes 1000) false))
  (testing "returns errors if any"
    ;; purposeful `registry2` path suffix to create exception
    (binding [*registry-url* "https://replicate.npmjs.com/registry2"]
      (callback-test (partial poll-for-changes 1000) true))))
