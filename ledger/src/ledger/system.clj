(ns ledger.system
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [common-clj.components.config.edn-config :as edn-config]
            [common-clj.components.consumer.in-memory-consumer :as in-memory-consumer]
            [common-clj.components.consumer.kafka-consumer :as kafka-consumer]
            [common-clj.components.docstore-client.dynamo-docstore-client
             :as dynamo-docstore-client]
            [common-clj.components.docstore-client.in-memory-docstore-client
             :as in-memory-docstore-client]
            [common-clj.components.http-server.http-server :as http-server]
            [ledger.ports.consumer :refer [consumer-topics]]
            [ledger.ports.http-server :refer [routes]]))

(defn merge-vec [& args] (vec (apply concat args)))

(def app-components
  [:db])

(def system
  (component/system-map
   :config            (edn-config/new-config)
   
   :db                (component/using
                        (dynamo-docstore-client/new-docstore-client)
                        [:config])
      
   :consumer          (component/using
                        (kafka-consumer/new-consumer consumer-topics)
                        (merge-vec app-components [:config]))
   
   :http-server       (component/using
                        (http-server/new-http-server routes)
                        (merge-vec app-components [:config]))))

(def test-system
  (merge
   system
   (component/system-map
    :config          (edn-config/new-config :test)

    :db              (component/using
                      (in-memory-docstore-client/new-docstore-client)
                      [:config])

    :consumer        (component/using
                      (in-memory-consumer/new-consumer consumer-topics)
                      (merge-vec app-components [:config :db])))))

(def -main (partial component/start system))
(def run-dev (partial component/start system))
