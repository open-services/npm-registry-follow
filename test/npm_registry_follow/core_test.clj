(ns npm-registry-follow.core-test
  (:require [clojure.test :refer :all]
            [npm-registry-follow.core :refer :all]))

(def expected-changes 10)

(deftest listen-for-changes-test
  (testing "can read a list of package changes"
    (let [changes (atom [])
          done (promise)]
      (def stop (listen-for-changes
                  (fn [id]
                    (swap! changes conj id)
                    (println "got item")
                    (when (> (count @changes) (- expected-changes 1))
                      (is (= expected-changes (count @changes)))
                      ;; all items are unique
                      (is (= (count (set @changes)) (count @changes)))
                      (deliver done true)
                      (stop)))))
      (println "Waiting for test to finish...")
      @done)))
