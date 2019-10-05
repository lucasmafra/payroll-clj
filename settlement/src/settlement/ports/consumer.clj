(ns settlement.ports.consumer
  (:require [settlement.schemata.settlement :as s-settlement]
            [settlement.controllers.settlement :as c-settlement]))

(defn batch-settle!
  [{:keys [batch-settlement/as-of batch-settlement/id]} components]
  (c-settlement/batch-settle! as-of id components))

(def topics
  {:batch-settle
   {:handler batch-settle!
    :schema  s-settlement/BatchSettleMessage}})
