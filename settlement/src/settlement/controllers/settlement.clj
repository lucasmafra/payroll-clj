(ns settlement.controllers.settlement
  (:require [settlement.domain.settlement :as d-settlement]
            [settlement.adapters.settlement :as a-settlement]
            [common-clj.components.http-client.protocol :as hc-pro]
            [common-clj.components.producer.protocol :as producer-pro]
            [common-clj.time :as time]))

(defn- fetch-employees [{:keys [http-client]}]
  (hc-pro/request http-client :get-all-employees))

(defn- fetch-transactions [{:keys [employee/id]} {:keys [http-client]}]
  (hc-pro/request http-client :get-transactions {:employee-id id}))

(defn- settle-transactions! [employee settlement {:keys [producer]}]
  (let [message (a-settlement/->settle-transactions-message employee settlement)]
    (producer-pro/produce! producer :settle-transactions message)))

(defn- execute-payment! [employee settlement {:keys [producer]}]
  (let [message (a-settlement/->execute-payment-message
                 employee settlement :payment-method/deposit)]
    (producer-pro/produce! producer :execute-payment message)))

(defn- process-employee! [employee as-of components]
  (let [{:keys [transactions]} (fetch-transactions employee components)
        settlement             (d-settlement/settle transactions as-of)
        reference-date         (time/local-date-time->local-date as-of)]
    (when (and (d-settlement/is-pay-day? employee reference-date)
               (d-settlement/positive-settlement? settlement))
      (settle-transactions! employee settlement components)
      (execute-payment! employee settlement components))))

(defn- create-batch-report! [batch-id {:keys [producer]}]
  (producer-pro/produce! producer :create-batch-report {:batch-settlement/id batch-id}))

(defn batch-settle!
  [as-of batch-id components]
  (let [{:keys [employees]} (fetch-employees components)]
    (doseq [employee employees]
      (process-employee! employee as-of components))
    (create-batch-report! batch-id components)))
