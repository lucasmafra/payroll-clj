(ns settlement.domain.settlement
  (:require [schema.core :as s]
            [settlement.schemata.settlement :as s-settlement]
            [common-clj.time :as time])
  (:import (java.time LocalDate LocalDateTime)))

(defn- not-settled-yet [entry]
  (not (:settled-at entry)))

(defn- mark-as-settled [as-of]
  #(assoc % :settled-at as-of))

(defn- balance [entries]
  (reduce #(+ %1 (:amount %2)) 0 entries))

(defn- at-least-two-weeks-ago? [ref-date now]
  (not (time/before?
        (time/minus now (time/days 14))
        ref-date)))

(s/defn settle :- s-settlement/Settlement
  [ledger-entries :- [s-settlement/LedgerEntry]
   as-of :- LocalDateTime]
  (let [settled-entries (->> ledger-entries
                             (filter not-settled-yet)
                             (map (mark-as-settled as-of)))]
    {:balance (balance settled-entries)
     :settled settled-entries
     :as-of   as-of}))

(s/defn is-pay-day? :- s/Bool
  ([contract-type :- s-settlement/ContractType as-of :- LocalDate]
   (is-pay-day? contract-type as-of nil))
  
  ([contract-type :- s-settlement/ContractType
    as-of :- LocalDate
    last-settlement :- (s/maybe s-settlement/Settlement)]
   (let [is-first-settlement? (not last-settlement)
         last-settlement-date (if last-settlement (time/local-date-time->local-date
                                                   (:as-of last-settlement)))]
     (contract-type
      {:salary           (time/last-day-of-month? as-of)
       
       :hourly-rate      (time/friday? as-of)
       
       :sales-commission (and (time/friday? as-of)
                              (or is-first-settlement?
                                  (at-least-two-weeks-ago? last-settlement-date
                                                           as-of)))}))))
