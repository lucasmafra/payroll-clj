(ns settlement.domain.settlement
  (:require [schema.core :as s]
            [settlement.schemata.settlement :as s-settlement]
            [common-clj.time :as time])
  (:import (java.time LocalDate LocalDateTime)))

(defn- not-settled-yet [transaction]
  (not (:transaction/settled-at transaction)))

(defn- balance [transactions]
  (reduce #(+ %1 (:transaction/amount %2)) 0 transactions))

(defn- at-least-two-weeks-ago? [ref-date now]
  (not (time/before?
        (time/minus now (time/days 14))
        ref-date)))

(s/defn settle :- s-settlement/Settlement
  [transactions :- [s-settlement/Transaction]
   as-of :- LocalDateTime]
  (let [to-settle (filter not-settled-yet transactions)]
    {:settlement/balance        (balance to-settle)
     :settlement/transactions   to-settle
     :settlement/reference-date as-of}))

(s/defn is-pay-day? :- s/Bool
  ([employee :- s-settlement/Employee as-of :- LocalDate]
   (is-pay-day? employee as-of nil))
  
  ([{:keys [employee/contract-type]} :- s-settlement/Employee
    as-of :- LocalDate
    last-settlement :- (s/maybe s-settlement/Settlement)]
   (let [first-settlement?    (nil? last-settlement)
         last-settlement-date (if last-settlement
                                (time/local-date-time->local-date
                                 (:settlement/reference-date last-settlement)))]
     (contract-type
      {:contract-type/salary (time/last-day-of-month? as-of)
       
       :contract-type/hourly-rate (time/friday? as-of)
       
       :contract-type/sales-commission (and (time/friday? as-of)
                                            (or first-settlement? (at-least-two-weeks-ago?
                                                                   last-settlement-date
                                                                   as-of)))}))))

(s/defn positive-settlement? :- s/Bool
  [{:keys [settlement/balance]} :- s-settlement/Settlement]
  (> balance 0))
