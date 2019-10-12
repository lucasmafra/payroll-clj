(ns settlement.ports.db
  (:require [schema.core :as s]
            [common-clj.components.docstore-client.protocol :as dc-pro]
            [settlement.schemata.settlement :as s-settlement]))

(defn create-batch! [batch-id as-of {:keys [db]}]
  (dc-pro/put-item! db :settlement/batches {:batch-settlement/id batch-id
                                            :batch-settlement/as-of as-of}))

(defn update-batch! [batch {:keys [db]}]
  (dc-pro/put-item! db :settlement/batches batch))

(defn mark-batch-as-processed! [batch now components]
  (update-batch! (assoc batch :batch-settlement/processed-at now) components))

(defn fetch-batch [batch-id {:keys [db]}]
  (dc-pro/get-item db
                   :settlement/batches
                   {:batch-settlement/id batch-id}
                   {:schema-resp s-settlement/BatchSettlement}))

(defn fetch-settled-transactions [{:keys [employee/id]} {:keys [db]}]
  (dc-pro/query db
                :settlement/transactions
                {:settlement/employee-id id}
                {:schema-resp #{s-settlement/Transaction}}))

(defn fetch-last-settlement [{:keys [employee/id]} {:keys [db]}]
  (first (dc-pro/query db
                       :settlement/history
                       {:settlement/employee-id id}
                       {:schema-resp #{s-settlement/Settlement}
                        :order       :desc})))

(def tables
  {:settlement/batches
   {:primary-key [:batch-settlement/id :s]}

   :settlement/history
   {:primary-key   [:settlement/employee-id :s]
    :secondary-key [:settlement/as-of :s]}

   :settlement/transactions
   {:primary-key [:settlement/employee-id :s]}})
