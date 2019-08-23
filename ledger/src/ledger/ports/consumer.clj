(ns ledger.ports.consumer
  (:require [schema.core :as s]
            [ledger.schemata.ledger :as schemata.ledger]
            [ledger.controllers.ledger :as controllers.ledger]))

(s/defn on-create-ledger-entry-arrived!
  [{:keys [employee-id entry]} :- schemata.ledger/CreateLedgerEntryMessage
   components]
  (controllers.ledger/create-new-entry! employee-id entry components))

(def consumer-topics
  {:create-ledger-entry {:handler on-create-ledger-entry-arrived!
                         :schema  schemata.ledger/CreateLedgerEntryMessage}})
