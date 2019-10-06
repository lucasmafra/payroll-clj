(ns settlement.system
  (:require [com.stuartsierra.component :as component]
            [common-clj.components.config.edn-config :as edn-config]
            [common-clj.components.consumer.kafka-consumer :as kafka-consumer]
            [common-clj.components.producer.kafka-producer :as kafka-producer]
            [settlement.ports.http-server :as p-hs]
            [settlement.ports.http-client :as p-hc]
            [settlement.ports.producer :as p-producer]
            [settlement.ports.consumer :as p-consumer]
            [common-clj.components.http-client.in-memory-http-client :as im-hc]
            [common-clj.components.consumer.in-memory-consumer :as im-consumer]
            [common-clj.components.producer.in-memory-producer :as im-producer]
            [common-clj.components.http-server.http-server :as hs]
            [common-clj.components.docstore-client.dynamo-docstore-client :as dynamo-dc]
            [common-clj.components.docstore-client.in-memory-docstore-client :as im-dc]))

(defn merge-vec [& args] (vec (apply concat args)))

(def app-components [:producer :http-client :db])

(def system
  (component/system-map
   :config      (edn-config/new-config)
   
   :http-server (component/using
                 (hs/new-http-server p-hs/routes)
                 (merge-vec app-components [:config]))
   
   :http-client (im-hc/new-http-client p-hc/endpoints)

   :consumer    (component/using
                 (kafka-consumer/new-consumer p-consumer/topics)
                 (merge-vec app-components [:config]))

   :producer    (component/using
                 (kafka-producer/new-producer p-producer/topics)
                 [:config])

   :db          (component/using
                 (dynamo-dc/new-docstore-client)
                 [:config])))

(def test-system
  (merge
   system
   (component/system-map
    :config      (edn-config/new-config :test)

    :http-client (im-hc/new-http-client p-hc/endpoints)

    :consumer    (component/using
                  (im-consumer/new-consumer p-consumer/topics)
                  (merge-vec app-components [:config]))

    :producer    (component/using
                  (im-producer/new-producer p-producer/topics)
                  [:config])

    :db          (component/using
                  (im-dc/new-docstore-client)
                  [:config]))))

(def -main (partial component/start system))
(def run-dev (partial component/start system))
