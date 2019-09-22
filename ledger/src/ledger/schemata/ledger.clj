(ns ledger.schemata.ledger
  (:require [schema.core :as s])
  (:import (java.math BigDecimal)
           (java.time LocalDate)))

(def Transaction
  {:transaction/control-key                  s/Uuid
   :transaction/category                     s/Keyword
   :transaction/amount                       BigDecimal
   :transaction/reference-date               LocalDate
   (s/optional-key :transaction/description) s/Str})

(def CreateTransactionMessage
  {:ledger/employee-id s/Uuid
   :ledger/transaction Transaction})

(def GetTransactionsResponse
  {:ledger/employee-id  s/Uuid
   :ledger/transactions [Transaction]})
