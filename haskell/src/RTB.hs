{-
Created       : 2014 Jul 17 (Thu) 08:38:10 by Harold Carr.
Last Modified : 2014 Aug 14 (Thu) 19:24:28 by Harold Carr.

- based on
  - http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
  - threepenny-gui MissingDollars sample
-}

{-# LANGUAGE RecursiveDo #-}

module RTB where

import qualified Data.Map                    as Map
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

    -- display elements
    (currentSub, subLayout, hSubFillLB) <- mkSPO SUB doSelectionQuery
    (currentPre, preLayout, hPreFillLB) <- mkSPO PRE doSelectionQuery
    (currentObj, objLayout, hObjFillLB) <- mkSPO OBJ doSelectionQuery

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
    updateDisplay "http://localhost:3030/ds/query" "?subject" "?predicate" "?object"

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
         , [ row [ element subLayout, element preLayout, element objLayout ] ]
         , [ element frameset ]
         ]

mkSPO :: SPOType
         -> ((SPOType, String) -> UI ())
         -> UI ( UI.Element
               , UI.Element
               , Handler [(String, BindingValue)]
               )
mkSPO spoType doSelectionQuery = mdo
    clrBtn  <- UI.button #+ [string "*"]
    current <- UI.input  # set (attr "size") "40" # set (attr "type") "text"
    xpdBtn  <- UI.button #+ [string "+"]
    listBox <- UI.listBox bLBItems
                          (pure Nothing)
                          bDisplayDI
    element listBox # set (attr "size") "10" # set style [("width","300px")]

    let dbFill      :: [(String,BindingValue)] -> DB DI -> DB DI
        dbFill ss _ = foldr dbCreate  dbEmpty ss

    (eFillLB, hFillLB) <- liftIO newEvent

    -- bDB :: Behavior (DB DI)
    bDB <- accumB dbEmpty $ concatenate <$> unions
        [ dbFill    <$> eFillLB
        ]

    on UI.selectionChange (getElement listBox) $ \x -> case x of
        Nothing -> return ()
        Just i  -> do db0 <- currentValue bDB
                      let (Just (s,_)) = dbLookup i db0
                      doSelectionQuery (spoType, s)
                      UI.setFocus $ getElement listBox

    let bLookup :: Behavior (DBKey -> Maybe DI)
        bLookup = flip dbLookup <$> bDB

        bDisplayDI :: Behavior (DBKey -> UI Element)
        bDisplayDI = (UI.string .) <$> (maybe "" showDI .) <$> bLookup

        bLBItems :: Behavior [DBKey]
        bLBItems = dbKeys <$> bDB

    layout <- column [ row [ element clrBtn, element current ]
                     , element xpdBtn
                     , element listBox
                     ]

    return (current, layout, hFillLB)

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

doRDFQuery :: String -> String -> String -> String
              -> IO ( [(String, BindingValue)]
                    , [(String, BindingValue)]
                    , [(String, BindingValue)]
                    )
doRDFQuery url _ _ _ = ttt url

-- End of file.
