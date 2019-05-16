(ns npm-registry-follow.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def registry-url "https://replicate.npmjs.com/registry")

(def stream-url (format "%s/_changes?include_docs=false&since=now&feed=continuous" registry-url))
(def poll-url #(format "%s/_changes?since=%s" registry-url %))
(def last-seq-url (format "%s/_changes?since=now" registry-url))

;; read changes from the npm registry
;; calls `callback` for each change, with a string of the module name
;; returns a function that can be called to stop listening for changes
;; the stop function blocks until stream been closed
(defn listen-for-changes [callback]
  (let [res (clojure.java.io/input-stream (java.net.URL. stream-url))]
    (future
      (with-open [rdr (clojure.java.io/reader res)]
        (doseq [line (line-seq rdr)]
          (if (= line "")
            (do
              (.close res)
              (listen-for-changes callback))
            (callback (:id (json/read-json line true)))))))
    (fn [] (future (.close res)))))

(defn get-changes-since [last-seq]
  (json/read-json (slurp (poll-url last-seq))))

(defn get-last-seq []
  (:last_seq (json/read-json (slurp last-seq-url))))

(comment
  (get-changes-since 1275194)
  (get-last-seq))

;; also reads changes from the npm registry
;; calls `callback` for each change, with a string of the module name
;; returns a function that can called to stop listening for changes
;; defaults to polling every 10 seconds
(defn poll-for-changes
  ([callback]
   (poll-for-changes (* 10 1000) callback))
  ([polling-interval callback]
   (let [poll? (atom true)]
     (future
       (let [last-seq (atom (get-last-seq)) ]
         (while @poll?
           (let [res (get-changes-since @last-seq)]
             (doseq [item (:results res)]
               (callback (:id item)))
             (reset! last-seq (:last_seq res))
             (Thread/sleep polling-interval)))))
     (fn [] (reset! poll? false)))))

(comment
  (def stop (listen-for-changes #(println %)))
  (stop)
  (def stop2 (poll-for-changes #(println %)))
  (stop2))
