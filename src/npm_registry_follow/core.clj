(ns npm-registry-follow.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def registry-url "https://replicate.npmjs.com/registry")

(def url (format "%s/_changes?include_docs=false&since=now&feed=continuous" registry-url))

;; read changes from the npm registry
;; calls `callback` for each change, with a string of the module name
;; returns a function that can be called to stop listening for changes
;; the stop function blocks until stream been closed
(defn listen-for-changes [callback]
  (let [res (clojure.java.io/input-stream (java.net.URL. url))]
    (future
      (with-open [rdr (clojure.java.io/reader res)]
        (doseq [line (line-seq rdr)]
          (if (= line "")
            (do
              (.close res)
              (listen-for-changes callback))
            (callback (:id (json/read-json line true)))))))
    (fn [] (future (.close res)))))
