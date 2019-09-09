(ns flows.aux
  (:require [ledger.components.ledger-repository.protocol
             :as ledger-repository.protocol]
            [selvage.midje.flow :refer [*world*]]))

(defn get-ledger [employee-id]
  (let [ledger-repository (-> *world* :system :ledger-repository)]
    (ledger-repository.protocol/get-ledger ledger-repository employee-id)))

(defn create-entry!
  [employee-id entry world]
  (let [ledger-repository (-> world :system :ledger-repository)]
    (ledger-repository.protocol/add-entry! ledger-repository
                                           employee-id
                                           entry)
    world))
