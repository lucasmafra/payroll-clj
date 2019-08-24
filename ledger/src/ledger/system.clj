(ns ledger.system
  (:require [com.stuartsierra.component :as component]
            [common-clj.components.consumer.kafka-consumer :as kafka-consumer]
            [common-clj.components.consumer.in-memory-consumer :as in-memory-consumer]
            [common-clj.components.config.edn-config :as edn-config]
            [common-clj.components.docstore-client.dynamo-docstore-client
             :as dynamo-docstore-client]
            [ledger.ports.consumer :refer [consumer-topics]]
            [ledger.components.ledger-repository.ledger-repository :as ledger-repository]
            [common-clj.components.docstore-client.in-memory-docstore-client
             :as in-memory-docstore-client]))

(defn merge-vec [& args] (vec (apply concat args)))

(def app-components
  [:ledger-repository])

(def system
  (component/system-map
   :config            (edn-config/new-config)
   :docstore-client   (component/using
                       (dynamo-docstore-client/new-docstore-client)
                       [:config])
   :ledger-repository (component/using
                       (ledger-repository/new-ledger-repository)
                       [:docstore-client])
   :consumer        (component/using
                     (kafka-consumer/new-consumer consumer-topics)
                     (merge-vec app-components [:config]))))

(def test-system
  (merge
   system
   (component/system-map
    :consumer        (component/using
                      (in-memory-consumer/new-consumer consumer-topics)
                      (merge-vec app-components [:config]))
    :docstore-client (in-memory-docstore-client/new-docstore-client))))
