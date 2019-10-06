(ns settlement.ports.consumer
  (:require [settlement.schemata.settlement :as s-settlement]
            [settlement.controllers.settlement :as c-settlement]))

(defn process-batch!
  [{:keys [batch-settlement/id]} components]
  (c-settlement/process-batch! id components))

(def topics
  {:process-batch
   {:handler process-batch!
    :schema  s-settlement/ProcessBatchMessage}})
