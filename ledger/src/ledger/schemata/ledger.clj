(ns ledger.schemata.ledger
  (:require [schema.core :as s])
  (:import (java.math BigDecimal)
           (java.time LocalDate)))

(def LedgerEntry
  {:control-key                  s/Uuid
   :type                         s/Keyword
   :amount                       BigDecimal
   :reference-date               LocalDate
   (s/optional-key :description) s/Str})

(def CreateLedgerEntryMessage
  {:employee-id s/Uuid
   :entry       LedgerEntry})

(def GetLedgerResponse
  {:employee-id s/Uuid
   :entries [LedgerEntry]})
