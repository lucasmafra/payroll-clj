(ns ledger.controllers.ledger
  (:require [ledger.ports.db :as db]
            [ledger.schemata.ledger :as s-ledger]
            [schema.core :as s]))

(s/defn create-transaction!
  [employee-id :- s/Uuid
   {:keys [transaction/control-key] :as new-transaction} :- s-ledger/Transaction
   {:keys [db]}]
  (when-not (db/get-transaction employee-id control-key db)
    (db/save-transaction! employee-id new-transaction db)))

(s/defn get-transactions
  [employee-id :- s/Uuid {:keys [db]}]
  (db/get-transactions employee-id db))
