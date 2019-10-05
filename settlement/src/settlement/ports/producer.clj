(ns settlement.ports.producer
  (:require [settlement.schemata.settlement :as s-settlement]))

(def topics
  {:execute-payment     s-settlement/ExecutePaymentMessage
   :settle-transactions s-settlement/SettleTransactionsMessage
   :create-batch-report s-settlement/CreateBatchReportMessage})
