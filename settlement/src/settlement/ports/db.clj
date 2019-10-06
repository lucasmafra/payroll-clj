(ns settlement.ports.db
  (:require [schema.core :as s]
            [common-clj.components.docstore-client.protocol :as dc-pro]
            [settlement.schemata.settlement :as s-settlement]))

(defn create-batch! [batch-id as-of db]
  (dc-pro/put-item! db
                    :settlement/batches
                    {:batch-settlement/id batch-id
                     :batch-settlement/as-of as-of}))

(defn update-batch! [batch db]
  (dc-pro/put-item! db
                    :settlement/batches
                    batch))

(defn get-batch [batch-id db]
  (dc-pro/get-item db
                   :settlement/batches
                   {:batch-settlement/id batch-id}
                   {:schema-resp s-settlement/BatchSettlement}))
