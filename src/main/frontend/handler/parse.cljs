(ns frontend.handler.parse
  (:require [electron.ipc :as ipc]
            [frontend.config :as config]
            [frontend.util :as util]
            [promesa.core :as p]
            [frontend.mobile.util :as mobile-util]
            [cljs-bean.core :as bean]
            ["webworker-promise" :as Webworker-promise]))

(defonce *worker (atom nil))

(defonce worker
  (p/let [static-path (if (and (util/electron?)
                               (= "file:" (.-protocol js/location)))
                        (ipc/ipc :getDirname)
                        "/static")
          path (str static-path "/js/parser-worker.js")
          path (if (or (util/electron?)
                       (mobile-util/is-native-platform?))
                 path
                 (config/asset-uri path))]
    (let [worker (Webworker-promise. (js/Worker. path))]
      (reset! *worker worker))))

(defn- parse!
  [content config]
  (p/let [result (.postMessage ^js @*worker (bean/->js [content config]))
          result' (js/JSON.parse result)]
    (bean/->clj result')))
