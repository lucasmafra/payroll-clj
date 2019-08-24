(ns ledger.controllers.ledger
  (:require [ledger.components.ledger-repository.protocol :as ledger-repository.protocol]
            [ledger.schemata.ledger :as schemata.ledger]
            [schema.core :as s]))

(s/defn create-new-entry!
  [employee-id :- s/Uuid
   {:keys [control-key] :as new-entry} :- schemata.ledger/LedgerEntry
   {:keys [ledger-repository]}]
  (let [entry (ledger-repository.protocol/get-entry ledger-repository employee-id control-key)]
    (when-not entry
      (ledger-repository.protocol/add-entry! ledger-repository employee-id new-entry))))
