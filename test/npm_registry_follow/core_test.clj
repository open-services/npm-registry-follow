(ns npm-registry-follow.core-test
  (:require [clojure.test :refer :all]
            [npm-registry-follow.core :refer :all]))

(deftest listen-for-changes-test
  (testing "can read a list of package changes"
    (let [changes (atom [])
          done (promise)]
      (def stop (listen-for-changes
                  (fn [id]
                    (swap! changes conj id)
                    (println "got item")
                    (when (> (count @changes) 1)
                      (is (= 2 (count @changes)))
                      (deliver done true)
                      (stop)))))
      (println "Waiting for test to finish...")
      @done)))
