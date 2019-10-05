(defproject settlement "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [lucasmafra/common-clj "0.28.0"]]
  :main ^{:skip-aot false} settlement.system
  :target-path "target/%s"
  :profiles {:uberjar    {:aot :all}
             :dev        {:aliases      {"lint-fix" ["do" "nsorg" "--replace," "kibit" "--replace"]}
                          :dependencies [[midje "1.9.8"]
                                         [nubank/matcher-combinators "1.0.0"]
                                         [nubank/selvage "1.0.0-BETA"]]
                          :plugins      [[lein-midje "3.2.1"]
                                         [lein-nsorg "0.3.0"]
                                         [lein-kibit "0.1.7"]]}
             :repl-start {:injections   [(require '[settlement.system :as system])
                                         (system/run-dev)]
                          :repl-options {:prompt  #(str "[settlement] " % "=> ")
                                         :timeout 300000
                                         :init-ns user}}}
  :aliases {"run-dev" ["with-profile" "+repl-start" "trampoline" "repl" ":headless"]})
