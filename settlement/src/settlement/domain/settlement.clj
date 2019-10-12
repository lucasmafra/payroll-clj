(ns settlement.domain.settlement
  (:require [clojure.set :refer [difference intersection]]
            [common-clj.time :as time]
            [schema.core :as s]
            [settlement.schemata.settlement :as s-settlement])
  (:import java.time.LocalDate))

(defn- balance [transactions]
  (reduce #(+ %1 (:transaction/amount %2)) 0M transactions))

(defn- two-weeks-ago [now] (time/minus now (time/days 14)))

(defn- at-least-two-weeks-ago? [{:keys [settlement/as-of]} now]
  (if as-of
    (not (time/before? (two-weeks-ago now) as-of))
    true))

(defn- remove-future-transactions [as-of transactions]
  (set (remove #(time/after? (:transaction/reference-date %) as-of) transactions)))

(s/defn settle :- s-settlement/Settlement
  [all-transactions :- #{s-settlement/Transaction}
   settled-transactions :- #{s-settlement/Transaction}
   as-of :- LocalDate]
  (let [not-settled (difference all-transactions settled-transactions)
        not-future  (remove-future-transactions as-of all-transactions)
        to-settle   (intersection not-settled not-future)]
    {:settlement/balance      (balance to-settle)
     :settlement/transactions to-settle
     :settlement/as-of        as-of}))

(s/defn pay-day? :- s/Bool
  [{:keys [employee/contract-type]} :- s-settlement/Employee
   as-of :- LocalDate
   last-settlement :- (s/maybe s-settlement/Settlement)]
  (contract-type
   {:contract-type/salary           (time/last-day-of-month? as-of)
    
    :contract-type/hourly-rate      (time/friday? as-of)
    
    :contract-type/sales-commission (and (time/friday? as-of)
                                         (at-least-two-weeks-ago? last-settlement as-of))}))

(s/defn positive-settlement? :- s/Bool
  [{:keys [settlement/balance]} :- s-settlement/Settlement]
  (> balance 0))
