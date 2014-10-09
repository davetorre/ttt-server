(defproject ttt-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [tic_tac_toe "0.1.0-SNAPSHOT"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [environ "1.0.0"]]
  :plugins [[lein-environ "1.0.0"]]
  :main ^:skip-aot ttt-server.core
  :target-path "target/%s"
  :resource-paths ["resources/HTTPServer.jar"]
  :profiles {:uberjar {:aot :all}
             :dev {:env {:db-config-file "resources/config_dev.edn"}}
             :test {:env {:db-config-file "resources/config_test.edn"}}}
  :aliases {"test" ["with-profile" "test" "test"]})
