(ns ledger.ports.consumer
  (:require [ledger.controllers.ledger :as c-ledger]
            [ledger.schemata.ledger :as s-ledger]
            [schema.core :as s]))

(s/defn create-transaction!
  [{:keys [ledger/employee-id ledger/transaction]} components]
  (c-ledger/create-transaction! employee-id transaction components))

(def topics
  {:create-transaction
   {:topic/handler create-transaction!
    :topic/schema  s-ledger/CreateTransactionMessage}})
