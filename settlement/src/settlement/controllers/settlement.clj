(ns settlement.controllers.settlement
  (:require [common-clj.time :as time]
            [common-clj.uuid :as uuid]
            [settlement.domain.settlement :as ds]
            [settlement.ports.http-client :as hc]
            [settlement.ports.db :as db]
            [settlement.ports.producer :as producer]))

(defn- process-employee! [employee now components]
  (let [all-transactions     (hc/fetch-all-transactions employee components)
        settled-transactions (db/fetch-settled-transactions employee components)
        last-settlement      (db/fetch-last-settlement employee components)
        today                (time/local-date-time->local-date now)
        new-settlement       (ds/settle all-transactions settled-transactions today)]
    (when (and (ds/pay-day? employee today)
               (ds/positive-settlement? new-settlement))
      (producer/produce-execute-payment-message! employee new-settlement components))))

(defn process-batch!
  [id components]
  (let [{:keys [batch/as-of batch/processed-at] :as batch} (db/fetch-batch id components)
        employees                                          (hc/fetch-employees components)
        now                                                (time/now)
        not-processed-yet                                  (nil? processed-at)]
    (when not-processed-yet
      (doseq [employee employees] (process-employee! employee as-of components))
      (db/mark-batch-as-processed! batch now components)
      (producer/produce-create-batch-report-message! id components))))

(defn create-batch-settlement!
  [as-of components]
  (let [batch-id  (uuid/random)
        now       (or as-of (time/now)) 
        new-batch (db/create-batch! batch-id now components)]    
    (producer/produce-process-batch-message! new-batch components)
    new-batch))
