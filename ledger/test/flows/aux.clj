(ns flows.aux
  (:require [common-clj.test-helpers :refer :all]
            [ledger.ports.db :as db]
            [selvage.midje.flow :refer [*world*]]))

(defn get-transactions [employee-id]
  (let [db (-> *world* :system :db)]
    (db/get-transactions employee-id db)))

(defn mock-transactions! [& args]
  (let [world (last args)
        keyvals (butlast args)]
    (doseq [[employee transaction] (partition 2 keyvals)]
      (db/save-transaction! employee transaction (-> world :system :db)))
    world))

(defn create-transaction-messages-arrived!
  [& args]
  (let [world (last args)
        keyvals (butlast args)]
    (doseq [[employee transaction] (partition 2 keyvals)]
      (message-arrived! :create-transaction {:ledger/employee-id employee
                                             :ledger/transaction transaction}
                        world))
    world))

(defn get-transactions-request-arrived! [employee world]
  (request-arrived!
   :get-transactions
   {:path-params {:employee-id employee}}
   world))
