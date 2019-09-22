(ns settlement.ports.db
  (:require [schema.core :as s]
            [common-clj.components.docstore-client.protocol :as dc.pro]))

(s/defn schedule-settlement!
  [employee-id at db]
  (dc.pro/put-item! db
                    :settlement/schedules
                    {:employee-id employee-id
                     :at          at}))
