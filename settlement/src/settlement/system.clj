(ns settlement.system
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
            [settlement.ports.http-server :as ports.http-server]))

(defn merge-vec [& args] (vec (apply concat args)))

(def app-components [])

(def system
  (component/system-map
   :config            (edn-config/new-config)
   :http-server       (component/using
                       (http-server/new-http-server ports.http-server/routes)
                       [:config])))

(def test-system
  (merge
   system
   (component/system-map
    :config          (edn-config/new-config :test))))

(def -main (partial component/start system))
(def run-dev (partial component/start system))
