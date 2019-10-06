(ns flows.aux
  (:require [settlement.ports.db :as db]
            [common-clj.components.http-client.in-memory-http-client :as im-hc]
            [common-clj.test-helpers :as th]
            [settlement.controllers.settlement :as c-settlement]))

(defn mock-employees! [& args]
  (let [world (last args)
        http-client (-> world :system :http-client)
        employees (butlast args)]
    (im-hc/mock-response! http-client :get-all-employees {:body {:employees employees}})
    world))

(defn mock-transactions!
  [& args]
  (let [world (last args)
        http-client (-> world :system :http-client)
        kvs         (butlast args)]
    (doseq [[{:keys [employee/id]} transactions] (partition 2 kvs)]
      (im-hc/mock-response!
       http-client :get-transactions {:employee-id id} {:body {:transactions transactions}}))
    world))

(defn mock-batch! [batch-id as-of world]
  (with-redefs [c-settlement/new-batch-id (constantly batch-id)]
    (th/request-arrived! :batch-settle {:body {:batch-settlement/as-of as-of}} world)))
