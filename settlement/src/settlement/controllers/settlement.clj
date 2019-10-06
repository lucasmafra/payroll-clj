(ns settlement.controllers.settlement
  (:require [settlement.domain.settlement :as d-settlement]
            [settlement.adapters.settlement :as a-settlement]
            [common-clj.components.http-client.protocol :as hc-pro]
            [common-clj.components.producer.protocol :as producer-pro]
            [common-clj.time :as time]
            [settlement.ports.db :as db]
            [common-clj.generators :as gen]
            [schema.core :as s]))

(defn- fetch-employees [{:keys [http-client]}]
  (hc-pro/request http-client :get-all-employees))

(defn- fetch-transactions [{:keys [employee/id]} {:keys [http-client]}]
  (hc-pro/request http-client :get-transactions {:employee-id id}))

(defn- produce-process-batch-message! [batch-id {:keys [producer]}]
  (producer-pro/produce! producer :process-batch {:batch-settlement/id batch-id}))

(defn- produce-settle-transactions-message! [employee settlement {:keys [producer]}]
  (let [message (a-settlement/->settle-transactions-message employee settlement)]
    (producer-pro/produce! producer :settle-transactions message)))

(defn- produce-execute-payment-message! [employee settlement {:keys [producer]}]
  (let [message (a-settlement/->execute-payment-message
                 employee settlement :payment-method/deposit)]
    (producer-pro/produce! producer :execute-payment message)))

(defn- produce-create-batch-report-message! [batch-id {:keys [producer]}]
  (producer-pro/produce! producer :create-batch-report {:batch-settlement/id batch-id}))

(defn- fetch-batch [batch-id {:keys [db]}] (db/get-batch batch-id db))

(defn- create-batch! [batch-id as-of {:keys [db]}] (db/create-batch! batch-id as-of db))

(defn- mark-batch-as-processed! [batch now {:keys [db]}]
  (db/update-batch! (assoc batch :batch-settlement/processed-at now) db))

(defn- new-batch-id [] (gen/generate s/Uuid))

(defn- process-employee! [employee as-of components]
  (let [{:keys [transactions]} (fetch-transactions employee components)
        settlement             (d-settlement/settle transactions as-of)
        reference-date         (time/local-date-time->local-date as-of)]
    (when (and (d-settlement/is-pay-day? employee reference-date)
               (d-settlement/positive-settlement? settlement))
      (produce-settle-transactions-message! employee settlement components)
      (produce-execute-payment-message! employee settlement components))))

(defn create-batch-settlement!
  [as-of components]
  (let [batch-id (new-batch-id)
        new-batch (create-batch! batch-id (or as-of (time/now)) components)]    
    (produce-process-batch-message! batch-id components)
    new-batch))

(defn process-batch!
  [id components]
  (let [batch                                                          (fetch-batch id components)
        {:keys [batch-settlement/as-of batch-settlement/processed-at]} batch
        {:keys [employees]}                                            (fetch-employees components)
        now                                                            (time/now)]
    (when-not processed-at
      (doseq [employee employees] (process-employee! employee as-of components))
      (mark-batch-as-processed! batch now components)
      (produce-create-batch-report-message! id components))))
