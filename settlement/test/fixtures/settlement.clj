(ns fixtures.settlement
  (:require [common-clj.generators :as gen]
            [schema.core :as s]
            [settlement.schemata.settlement :as s-settlement]))

(def last-day-of-month #date "2019-09-30")

(def last-day-of-month-9am #date-time "2019-09-30T09:00:00")

(def last-day-of-month-10am #date-time "2019-09-30T10:00:00")

(def batch-id (gen/generate s/Uuid))

(def another-batch-id (gen/generate s/Uuid))

; Salaried employees get paid on last day of month
(def employee<pay-day> (gen/complete {:employee/contract-type :contract-type/salary}
                                     s-settlement/Employee))

; Hourly rate employees don't get paid when it's not friday
(def employee<not-pay-day> (gen/complete {:employee/contract-type :contract-type/hourly-rate}
                                         s-settlement/Employee))

; Will mock a negative transaction for this employee
(def employee<pay-day-but-negative-balance> (gen/generate s-settlement/Employee))

(def employee-id<pay-day> (:employee/id employee<pay-day>))

(def transaction-a (gen/complete {:transaction/amount 3000M} s-settlement/Transaction))

(def transaction-b (gen/complete {:transaction/amount 2000M} s-settlement/Transaction))

(def transaction-c (gen/complete {:transaction/amount 2500M} s-settlement/Transaction))

(def negative-transaction (gen/complete {:transaction/amount -100M} s-settlement/Transaction))

(def amount<transaction-a> (:transaction/amount transaction-a))

(def amount<transaction-b> (:transaction/amount transaction-b))

(def amount<transaction-c> (:transaction/amount transaction-c))

(def salary-based-employee (gen/complete {:employee/contract-type :contract-type/salary}
                                         s-settlement/Employee))

(def hourly-based-employee (gen/complete {:employee/contract-type :contract-type/hourly-rate}
                                         s-settlement/Employee))

(def commission-based-employee (gen/complete
                                {:employee/contract-type :contract-type/sales-commission}
                                s-settlement/Employee))

(def last-day-of-current-month #date "2019-07-31")

(def last-day-of-current-month-9am #date-time "2019-07-31T09:00:00")

(def last-day-of-previous-month #date "2019-06-30")

(def last-day-of-previous-month-9am #date-time "2019-06-30T09:00:00")

(def a-week-ago #date "2019-07-24")

(def not-last-day-of-month #date "2019-07-30")

(def friday #date "2019-09-06")

(def not-friday #date "2019-09-05")

(def this-friday #date "2019-09-13")

(def last-friday #date "2019-09-06")

(def last-friday-9am #date-time "2019-09-06T09:00:00")

(def two-fridays-ago #date "2019-08-30")

(def negative-settlement (gen/complete {:settlement/balance -500M} s-settlement/Settlement))

(def zero-settlement (gen/complete {:settlement/balance 0M} s-settlement/Settlement))

(def positive-settlement (gen/complete {:settlement/balance 500M} s-settlement/Settlement))

(def as-of-future #date "9999-12-31")

(def as-of-today #date "2019-10-12")

(def as-of-yesterday #date "2019-10-11")

(def future-transaction (gen/complete {:transaction/amount 4700M
                                       :transaction/reference-date as-of-future}
                                      s-settlement/Transaction))

(def today-transaction (gen/complete {:transaction/amount 50000M
                                       :transaction/reference-date as-of-today}
                                      s-settlement/Transaction))

(def yesterday-transaction (gen/complete {:transaction/amount 12.320M
                                          :transaction/reference-date as-of-yesterday}
                                         s-settlement/Transaction))

(def last-settlement<this-friday>
  (gen/complete {:settlement/as-of this-friday} s-settlement/Settlement))

(def last-settlement<last-friday>
  (gen/complete {:settlement/as-of last-friday} s-settlement/Settlement))

(def last-settlement<two-fridays-ago>
  (gen/complete {:settlement/as-of two-fridays-ago} s-settlement/Settlement))
