(ns settlement.ports.producer
  (:require [common-clj.components.producer.protocol :as producer-pro]
            [settlement.adapters.settlement :as as]
            [settlement.schemata.settlement :as s-settlement]))

(defn produce-process-batch-message! [{:keys [batch-settlement/id]} {:keys [producer]}]
  (producer-pro/produce! producer :process-batch {:batch-settlement/id id}))

(defn produce-execute-payment-message! [employee settlement {:keys [producer]}]
  (let [message (as/->execute-payment-message employee settlement :payment-method/deposit)]
    (producer-pro/produce! producer :execute-payment message)))

(defn produce-create-batch-report-message! [batch-id {:keys [producer]}]
  (producer-pro/produce! producer :create-batch-report {:batch-settlement/id batch-id}))

(def topics
  {:execute-payment     s-settlement/ExecutePaymentMessage
   :settle-transactions s-settlement/SettleTransactionsMessage
   :create-batch-report s-settlement/CreateBatchReportMessage
   :process-batch       s-settlement/ProcessBatchMessage})
