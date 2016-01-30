(ns ip-bot.core
  (:gen-class)
  (:require [ip-bot.utils :refer [get-ip]])
  (:require [schejulure.core :as duler]))

(def blog-host-address "pacific-ridge-5380.herokuapp.com")
(def ip-list (atom #{}))

(defn get-current-ip-list
  "Return a set with ip addresses. Get them n (count) times with intrval (intrval)"
  [interval count]
  (loop [n 0]
    (when-not (= n count)
      (do
        (swap! ip-list conj (get-ip blog-host-address))
        ;(println @ip-list)
        (Thread/sleep interval)
        (recur (inc n))))))

(defn -main
  "start app loop"
  [& args]
  (let [fetch-from-heroku (partial get-current-ip-list 30000 10)]
    (duler/schedule {:hour (range 0 24 2)} fetch-from-heroku)))