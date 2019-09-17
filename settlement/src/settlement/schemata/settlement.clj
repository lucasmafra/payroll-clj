(ns settlement.schemata.settlement
  (:require [schema.core :as s])
  (:import java.math.BigDecimal
           (java.time LocalDate LocalDateTime)))

(defn- loose-enum [v] s/Keyword)

(def ContractType
  (s/enum :salary :hourly-rate :sales-commission))

(def ledger-entry-types
  #{:salary-compensation :service-charge :union-tax})

(def LedgerEntry
  {:control-key                 s/Uuid
   :type                        (loose-enum ledger-entry-types)
   :amount                      BigDecimal
   :reference-date              LocalDate
   (s/optional-key :settled-at) LocalDateTime})

(def Settlement
  {:balance BigDecimal
   :settled [LedgerEntry]
   :as-of   LocalDateTime})
