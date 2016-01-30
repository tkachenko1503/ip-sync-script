(ns ip-bot.utils
  (:require [clj-http.client :as client]
            [clojure.data.json :as json])
  (:import [java.net InetAddress]))


(defn by-name
  "Determines the IP address of a host, given the host's name."
  [^String host]
  (InetAddress/getByName host))

(defn get-ip-string
  "Return ip string representation"
  [ip-class]
  (.getHostAddress ip-class))

(def get-ip
  (comp get-ip-string by-name))


(defn reg-query-params
  "Return query params for reg.ru query"
  [name pass domain]
  {:input_format "json"
   :output_format "json"
   :io_encoding "utf8"
   :show_input_params 0
   :username name
   :password pass
   :domain_name domain})

(defn get-regru-data
  "Fetch data from reg.ru"
  [{:keys [name, pass, domain, url] :or {url "https://api.reg.ru/api/regru2/zone/get_resource_records"
                                         name "test"
                                         pass "test"
                                         domain "test.ru"}}]
  (let [params (reg-query-params name pass domain)]
    (:body (client/post url {:form-params params}))))

(defn extractIP
  "Return list of IP strings"
  [responseBody]
  (let [data (json/read-str responseBody :key-fn keyword)]
    (->> (get-in data [:answer :domains])
        (filter #(= "A" (get-in % [:rrs 0 :rectype])))
        (map #(get-in % [:rrs 0 :content])))))

(defn getIPs
  "Fetch and parse IP from reg.ru"
  [config]
  (let [body (get-regru-data config)]
    (extractIP body)))


(defn find-ip-diff
  "Find ip to add/remove"
  [actual current]
  ())