(ns settlement.system
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [common-clj.components.config.edn-config :as edn-config]))

(defn merge-vec [& args] (vec (apply concat args)))

(def app-components [])

(def system
  (component/system-map
   :config            (edn-config/new-config)))

(def test-system
  (merge
   system
   (component/system-map
    :config          (edn-config/new-config :test))))

(def -main (partial component/start system))
(def run-dev (partial component/start system))
