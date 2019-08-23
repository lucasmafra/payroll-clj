(ns ledger.controllers.ledger
  (:require [schema.core :as s]
            [ledger.schemata.ledger :as schemata.ledger]
            [ledger.components.ledger-repository.protocol :as ledger-repository.protocol]))

(s/defn create-new-entry!
  [employee-id :- s/Uuid
   entry :- schemata.ledger/LedgerEntry
   {:keys [ledger-repository]}]
  (ledger-repository.protocol/add-new-entry! ledger-repository employee-id entry))
