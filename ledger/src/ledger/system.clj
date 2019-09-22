(ns ledger.system
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [common-clj.components.config.edn-config :as edn-config]
            [common-clj.components.consumer.in-memory-consumer :as im-consumer]
            [common-clj.components.consumer.kafka-consumer :as kafka-consumer]
            [common-clj.components.docstore-client.dynamo-docstore-client
             :as dynamo-dc]
            [common-clj.components.docstore-client.in-memory-docstore-client :as im-dc]
            [common-clj.components.http-server.http-server :as hs]
            [ledger.ports.consumer :as p-consumer]
            [ledger.ports.http-server :as p-hs]))

(defn merge-vec [& args] (vec (apply concat args)))

(def app-components
  [:db])

(def system
  (component/system-map
   :config            (edn-config/new-config)
   
   :db                (component/using
                        (dynamo-dc/new-docstore-client)
                        [:config])
      
   :consumer          (component/using
                        (kafka-consumer/new-consumer p-consumer/topics)
                        (merge-vec app-components [:config]))
   
   :http-server       (component/using
                        (hs/new-http-server p-hs/routes)
                        (merge-vec app-components [:config]))))

(def test-system
  (merge
   system
   (component/system-map
    :config          (edn-config/new-config :test)

    :db              (component/using
                      (im-dc/new-docstore-client)
                      [:config])

    :consumer        (component/using
                      (im-consumer/new-consumer p-consumer/topics)
                      (merge-vec app-components [:config :db])))))

(def -main (partial component/start system))
(def run-dev (partial component/start system))
