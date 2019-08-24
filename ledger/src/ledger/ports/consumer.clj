(ns ledger.ports.consumer
  (:require [ledger.controllers.ledger :as controllers.ledger]
            [ledger.schemata.ledger :as schemata.ledger]
            [schema.core :as s]))

(s/defn on-create-ledger-entry-message
  [{:keys [employee-id entry]} :- schemata.ledger/CreateLedgerEntryMessage
   components]
  (controllers.ledger/create-new-entry! employee-id entry components))

(def consumer-topics
  {:create-ledger-entry {:handler on-create-ledger-entry-message
                         :schema  schemata.ledger/CreateLedgerEntryMessage}})
