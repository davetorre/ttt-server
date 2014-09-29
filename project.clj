(defproject ttt-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [tic_tac_toe "0.1.0-SNAPSHOT"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [hiccup "1.0.5"]]
  :resource-paths ["resources/httpserver-1.0-SNAPSHOT.jar"]
  :main ^:skip-aot ttt-server.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
