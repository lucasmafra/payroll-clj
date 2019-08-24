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

(def LedgerEntryDocument
  {:employee-id    s/Str
   :control-key    s/Str
   :type           s/Str
   :amount         s/Any
   :reference-date s/Str
   (s/optional-key :description) s/Str})

(def CreateLedgerEntryMessage
  {:employee-id s/Uuid
   :entry       LedgerEntry})
