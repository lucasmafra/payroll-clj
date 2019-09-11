(ns flows.aux
  (:require [ledger.ports.db :as db]
            [selvage.midje.flow :refer [*world*]]))

(defn get-ledger [employee-id]
  (let [db-component (-> *world* :system :db)]
    (db/get-ledger employee-id db-component)))

(defn create-entry!
  [employee-id entry world]
  (let [db-component (-> world :system :db)]
    (db/save-ledger-entry! employee-id entry db-component)
    world))
