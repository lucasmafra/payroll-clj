(ns flows.aux
  (:require [ledger.ports.db :as db]
            [common-clj.test-helpers :refer :all]
            [selvage.midje.flow :refer [*world*]]))

(defn get-transactions [employee-id]
  (let [db (-> *world* :system :db)]
    (db/get-transactions employee-id db)))

(defn create-transaction!
  [employee-id transaction world]
  (let [db (-> world :system :db)]
    (db/save-transaction! employee-id transaction db)
    world))

(defn create-transaction-messages-arrived!
  [& args]
  (let [world (last args)
        keyvals (butlast args)]
    (doseq [[employee transaction] (partition 2 keyvals)]
      (message-arrived! :transaction/create
                        {:ledger/employee-id employee
                         :ledger/transaction transaction}))))
