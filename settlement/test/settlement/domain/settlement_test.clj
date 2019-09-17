(ns settlement.domain.settlement-test
  (:require [schema.core :as s]
            [midje.sweet :refer :all]
            [common-clj.test-helpers :refer [random-uuid]]
            [settlement.domain.settlement :as settlement]))

(def last-day-of-current-month #date "2019-07-31")
(def last-day-of-current-month-9am #date-time "2019-07-31T09:00:00")
(def last-day-of-previous-month #date "2019-06-30")
(def last-day-of-previous-month-9am #date-time "2019-06-30T09:00:00")
(def a-week-ago #date "2019-07-24")
(def not-last-day-of-month #date "2019-07-30")
(def friday #date "2019-09-06")
(def not-friday #date "2019-09-05")
(def last-friday #date "2019-09-06")
(def last-friday-9am #date-time "2019-09-06T09:00:00")
(def this-friday #date "2019-09-13")
(def next-friday #date "2019-09-20")

(def current-salary
  {:control-key    (random-uuid)
   :type           :salary-compensation
   :amount         2000M
   :reference-date last-day-of-current-month})

(def previous-salary
  {:control-key    (random-uuid)
   :type           :salary-compensation
   :amount         2000M
   :reference-date last-day-of-previous-month
   :settled-at     last-day-of-previous-month-9am})

(def service-charge
  {:control-key    (random-uuid)
   :type           :service-charge
   :amount         -300M
   :reference-date a-week-ago})

(def current-salary-settled
  (assoc current-salary :settled-at last-day-of-current-month-9am))

(def service-charge-settled
  (assoc service-charge :settled-at last-day-of-current-month-9am))

(def ledger-entries [current-salary service-charge previous-salary])

(def last-settlement
  {:balance 0M
   :settled []
   :as-of   last-friday-9am})

(s/with-fn-validation
  (facts "settle"
    (fact "only not-settled entries are considered"
      (-> ledger-entries
          (settlement/settle last-day-of-current-month-9am)
          :settled)
      => [current-salary-settled service-charge-settled])
    (fact "balance is the sum of all entries to be settled"
      (-> ledger-entries
          (settlement/settle last-day-of-current-month-9am)
          :balance)
      => 1700M))

  (facts "is-pay-day?"
    (fact "salaried employees get paid in the last day of month"
      (settlement/is-pay-day? :salary last-day-of-current-month) => true
      (settlement/is-pay-day? :salary not-last-day-of-month) => false)

    (fact "hourly employees get paid on fridays"
      (settlement/is-pay-day? :hourly-rate friday) => true
      (settlement/is-pay-day? :hourly-rate not-friday) => false)

    (fact "commissioned employees get paid every two fridays"
      (settlement/is-pay-day? :sales-commission last-friday)
      => true

      (settlement/is-pay-day? :sales-commission last-friday nil)
      => true

      (settlement/is-pay-day? :sales-commission last-friday last-settlement)
      => false

      (settlement/is-pay-day? :sales-commission this-friday last-settlement)
      => false

      (settlement/is-pay-day? :sales-commission next-friday last-settlement)
      => true)))
