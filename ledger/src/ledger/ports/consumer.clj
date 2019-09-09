(ns ledger.ports.consumer
  (:require [ledger.controllers.ledger :as controllers.ledger]
            [ledger.schemata.ledger :as schemata.ledger]
            [schema.core :as s]))

(s/defn create-entry
  [{:keys [employee-id entry]} :- schemata.ledger/CreateLedgerEntryMessage
   components]
  (controllers.ledger/create-new-entry! employee-id entry components))

(def consumer-topics
  {:create-ledger-entry {:handler create-entry
                         :schema  schemata.ledger/CreateLedgerEntryMessage}})
