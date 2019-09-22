(ns settlement.schemata.settlement
  (:require [schema.core :as s])
  (:import java.math.BigDecimal
           (java.time LocalDate LocalDateTime)))

(def ContractType
  (s/enum :contract-type/salary
          :contract-type/hourly-rate
          :contract-type/sales-commission))

(def Transaction
  {:transaction/control-key                 s/Uuid
   :transaction/category                    s/Keyword
   :transaction/amount                      BigDecimal
   :transaction/reference-date              LocalDate
   (s/optional-key :transaction/settled-at) LocalDateTime})

(def Settlement
  {:settlement/balance      BigDecimal
   :settlement/transactions [Transaction]
   :settlement/created-at   LocalDateTime})

(def Scheduling
  {:scheduling/employee-id                 s/Uuid
   :scheduling/at                          LocalDateTime
   (s/optional-key :scheduling/handled-at) LocalDateTime})
