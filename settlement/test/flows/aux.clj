(ns flows.aux
  (:require [settlement.ports.db :as db]))

(defn mock-ledgers!
  [& args]
  (let [http-client (-> args last :http-client)
        keyvals (butlast args)]
    (doseq [[employee ledger] (partition 2 keyvals)]
      (mock-response))))

(defn schedule-settlement!
  [employee-id at world]
  (let [database (-> world :system :db)]
    (db/schedule-settlement! employee-id at database)
    world))


(mock-ledgers!
 :employee-a 100M
 :employee-b 200M
 :employee-c 140M
 :world)

(defrecord InMemoryHttpClient []
  (mock-response!
    1))
