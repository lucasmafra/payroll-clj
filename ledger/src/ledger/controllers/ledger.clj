(ns ledger.controllers.ledger
  (:require [ledger.ports.db :as db]
            [ledger.schemata.ledger :as schemata.ledger]
            [schema.core :as s]))

(s/defn create-new-entry!
  [employee-id :- s/Uuid
   {:keys [control-key] :as new-entry} :- schemata.ledger/LedgerEntry
   {:keys [db]}]
  (when-not (db/get-ledger-entry employee-id control-key db)
    (db/save-ledger-entry! employee-id new-entry db)))

(s/defn get-ledger
  [employee-id :- s/Uuid
   {:keys [db]}]
  (db/get-ledger employee-id db))
