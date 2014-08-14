{-
Created       : 2014 Jul 17 (Thu) 08:38:10 by Harold Carr.
Last Modified : 2014 Aug 13 (Wed) 20:00:49 by Harold Carr.

- based on
  - http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
  - threepenny-gui MissingDollars sample
-}

{-# LANGUAGE RecursiveDo #-}

module RTB where

import           Control.Monad               (replicateM)
import qualified Data.Map                    as Map
import           Data.Maybe
import           Data.RDF.Types              (Node (..))
import qualified Data.Text                   as T (pack)
import           Database.HSparql.Connection
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core
import           RTBQ

data SPOType = SUB | PRE | OBJ

main :: IO ()
main = startGUI defaultConfig $ \w -> do
    return w # set title "RDF Triple Browser"
    getBody w #+ [ mkLayout ]
    return ()

mkLayout :: UI Element
mkLayout  = mdo
    -- input elements
    sparqlEndpointURL <- UI.input  # set (attr "size") "175" # set (attr "type") "text"
    submitBtn         <- UI.button #+ [string "submit"]
    (xpdBtnSub  : xpdBtnPre  : xpdBtnObj  : _)
        <- replicateM 3 $ UI.button #+ [string "+"]

    (clrSub : clrPre : clrObj : _ )
        <- replicateM 3 $ UI.button #+ [string "*"]

    -- display elements
    (currentSub : currentPre : currentObj : _)
        <- replicateM 3 $ UI.input # set (attr "size") "40" # set (attr "type") "text"

    (subLB, hSubFillLB) <- mkListBox SUB doSelectionQuery
    (preLB, hPreFillLB) <- mkListBox PRE doSelectionQuery
    (objLB, hObjFillLB) <- mkListBox OBJ doSelectionQuery

    frame <- UI.frame # set (attr "name")   "top"
                      # set (attr "target") "top"
                      # set (attr "src")    "http://haroldcarr.com/"

    frameset <- UI.frameset #+ [ element frame ]

    -- submit button
    on UI.click submitBtn $ \_ -> do
        sparql <- get value sparqlEndpointURL
        updateDisplay sparql "?subject" "?predicate" "?object"
        doQuery

    -- update procedure
    let updateDisplay sp s p v = do {
        element sparqlEndpointURL # set value sp;
        element currentSub # set value s;
        element currentPre # set value p;
        element currentObj # set value v;
        return ()
    }

    -- initial values
    updateDisplay "enter SPARQL endpoint URL" "?subject" "?predicate" "?object"

    -- querying

    let doQuery = do {
        sparql     <- get value sparqlEndpointURL;
        s          <- get value currentSub;
        p          <- get value currentPre;
        v          <- get value currentObj;
        (sr,pr,vr) <- liftIO $ doRDFQuery sparql s p v;
        -- These are the MAGIC steps.  A Handler feeds events to their corresponding Event (from newEvent)
        liftIO $ hSubFillLB sr;
        liftIO $ hPreFillLB pr;
        liftIO $ hObjFillLB vr;
        return ()
    }

    let doSelectionQuery (spo, selection) = do {
        element (case spo of
                    SUB -> currentSub
                    PRE -> currentPre
                    OBJ -> currentObj)
                # set value selection;
        element frame # set (attr "src") selection;
        doQuery
    }

    grid [ [ row [ element sparqlEndpointURL, element submitBtn ] ]
         , [ row [ column [ row [ element clrSub, element currentSub ]
                          , element xpdBtnSub
                          , element subLB
                          ]
                 , column [ row [ element clrPre, element currentPre ]
                          , element xpdBtnPre
                          , element preLB
                          ]
                 , column [ row [ element clrObj, element currentObj ]
                          , element xpdBtnObj
                          , element objLB
                          ]
                 ]
           ]
         , [ element frameset ]
         ]

mkListBox :: SPOType
             -> ((SPOType,String) -> UI ())
             -> UI (UI.ListBox String, Handler [String])
mkListBox spoType doSelectionQuery = do
    (eFillLB, hFillLB) <- liftIO newEvent
    bFillLB <- stepper [] eFillLB
    listBox <- UI.listBox bFillLB
                          (pure Nothing)
                          (pure $ \it -> UI.span # set text it)
    element listBox # set (attr "size") "10" # set style [("width","300px")]

    on UI.selectionChange (getElement listBox) $ \x -> case x of
        Nothing -> return ()
        Just i  -> do items <- currentValue bFillLB
                      let selection = items !! i
                      doSelectionQuery (spoType, selection)
                      UI.setFocus $ getElement listBox
    return (listBox, hFillLB)

------------------------------------------------------------------------------
-- DB Model

type DBKey = Int
data DB a  = DB { nextKey :: !Int, db :: Map.Map DBKey a }

dbEmpty  :: DB a
dbEmpty  = DB 0 Map.empty

dbSize   :: DB a -> Int
dbSize   = Map.size . db

dbKeys   :: DB a -> [DBKey]
dbKeys   = Map.keys . db

dbCreate :: a -> DB a -> DB a
dbCreate x     (DB newkey db0) = DB (newkey+1) $ Map.insert newkey x db0

dbLookup :: DBKey -> DB a -> Maybe a
dbLookup key   (DB _      db0) = Map.lookup                       key      db0

------------------------------------------------------------------------------
-- What is stored in data base

type DI = (String, BindingValue)

showDI :: DI -> String
showDI (x,_) = x

------------------------------------------------------------------------------

doRDFQuery :: String -> String -> String -> String -> IO ([String],[String],[String])
doRDFQuery _ _ _ _ = tt

-- End of file.
