(ns settlement.schemata.settlement
  (:require [schema.core :as s])
  (:import (java.time LocalDate LocalDateTime)))

(def ContractType
  (s/enum :contract-type/salary
          :contract-type/hourly-rate
          :contract-type/sales-commission))

(def Transaction
  #:transaction
  {:control-key                             s/Uuid
   :category                                s/Keyword
   :amount                                  BigDecimal
   :reference-date                          LocalDate
   (s/optional-key :transaction/settled-at) LocalDateTime})

(def Settlement
  #:settlement
  {:balance        BigDecimal
   :transactions   [Transaction]
   :reference-date LocalDateTime})

(def Employee
  #:employee
  {:id            s/Uuid
   :contract-type ContractType})

(def PaymentMethod (s/enum :payment-method/deposit))

(def BatchSettleMessage
  #:batch-settlement
  {:as-of LocalDateTime
   :id    s/Uuid})

(def ExecutePaymentMessage
  #:payment
  {:recipient s/Uuid
   :amount    BigDecimal
   :method    PaymentMethod})

(def SettleTransactionsMessage
  #:settlement
  {:employee-id  s/Uuid
   :transactions [s/Uuid]})

(def CreateBatchReportMessage
  #:batch-settlement
  {:id s/Uuid})
