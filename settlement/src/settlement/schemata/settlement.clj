(ns settlement.schemata.settlement
  (:require [schema.core :as s])
  (:import (java.time LocalDate LocalDateTime)))

(def ContractType
  (s/enum :contract-type/salary
          :contract-type/hourly-rate
          :contract-type/sales-commission))

(def Transaction
  #:transaction
  {:control-key    s/Uuid
   :category       s/Keyword
   :amount         BigDecimal
   :reference-date LocalDate})

(def Settlement
  #:settlement
  {:balance      BigDecimal
   :transactions #{Transaction}
   :as-of        LocalDate})

(def Employee
  #:employee
  {:id            s/Uuid
   :contract-type ContractType})

(def PaymentMethod (s/enum :payment-method/deposit))

(def ProcessBatchMessage
  #:batch-settlement
  {:id s/Uuid})

(def ExecutePaymentMessage
  #:payment
  {:recipient   s/Uuid
   :amount      BigDecimal
   :control-key s/Uuid
   :method      PaymentMethod})

(def SettleTransactionsMessage
  #:settlement
  {:employee-id  s/Uuid
   :transactions [s/Uuid]})

(def CreateBatchReportMessage
  #:batch-settlement
  {:id s/Uuid})

(def BatchSettlement
  #:batch-settlement
  {:id                                             s/Uuid
   :as-of                                          LocalDateTime
   (s/optional-key :batch-settlement/processed-at) LocalDateTime})

(def BatchSettleRequest
  (s/maybe
   #:batch-settlement
   {(s/optional-key :batch-settlement/as-of) LocalDateTime}))

(def BatchSettleResponse
  #:batch-settlement
  {:id    s/Uuid
   :as-of LocalDateTime})
