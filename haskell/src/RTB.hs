{-
Created       : 2014 Jul 17 (Thu) 08:38:10 by Harold Carr.
Last Modified : 2014 Aug 15 (Fri) 08:36:46 by Harold Carr.

- based on
  - http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
  - threepenny-gui MissingDollars sample
-}

{-# LANGUAGE RecursiveDo #-}

module RTB where

import qualified Data.Map                    as Map
import           Data.Maybe                  (fromMaybe)
import           Data.RDF.Types              (Node (..))
import qualified Data.Text                   as T (pack)
import           Database.HSparql.Connection
import           Debug.Trace
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core
import           RTBQ

hcDebug :: c -> String -> c
hcDebug = flip trace

data SPOType = SUB | PRE | OBJ

instance Show SPOType where
    show SUB = "?subject"
    show PRE = "?predicate"
    show OBJ = "?object"

main :: IO ()
main = startGUI defaultConfig $ \w -> do
    return w # set title "RDF Triple Browser"
    getBody w #+ [ mkLayout ]
    return ()

mkLayout :: UI Element
mkLayout  = mdo
    -- input elements
    sparqlEndpointURL <- UI.input  # set (attr "size") "175" # set (attr "type") "text"
                                   # set (attr "value") endPoint
    submitBtn         <- UI.button #+ [string "submit"]

    -- display elements
    (_, subLayout, hSubFillLB) <- mkSPOPanel SUB
    (_, preLayout, hPreFillLB) <- mkSPOPanel PRE
    (_, objLayout, hObjFillLB) <- mkSPOPanel OBJ

    frame <- UI.frame # set (attr "name")   "top"
                      # set (attr "target") "top"
                      # set (attr "src")    "http://haroldcarr.com/"

    frameset <- UI.frameset #+ [ element frame ]

    -- submit button
    on UI.click submitBtn $ \_ -> do
        doQuery

    -- querying

    let doQuery = do {
        sparql     <- get value sparqlEndpointURL;
        liftIO $ putStrLn ("doQuery " ++ sparql);
        (sr,pr,vr) <- liftIO $ doRDFQuery1 sparql (show SUB) (show PRE) (show OBJ);
        -- These are the MAGIC steps.  A Handler feeds events to their corresponding Event (from newEvent)
        liftIO $ hSubFillLB sr;
        liftIO $ hPreFillLB pr;
        liftIO $ hObjFillLB vr;
        return ()
    }

    grid [ [ row [ element sparqlEndpointURL, element submitBtn ] ]
         , [ row [ element subLayout, element preLayout, element objLayout ] ]
         , [ element frameset ]
         ]

mkSPOPanel :: SPOType
--           -> ((SPOType, String) -> UI ())
           -> UI ( UI.Element
                 , UI.Element
                 , Handler [(String, BindingValue)]
                 )
mkSPOPanel spoType = mdo
    let bad = ("NOT SUPPOSED TO HAPPEN", Bound (UNode (T.pack "BAD")))
        decide :: DB DI -> String
        decide db0 | dbSize db0 > 1 = show spoType
                   | otherwise      = fst $ fromMaybe bad (dbLookup 0 db0) `hcDebug` "decide"

        dataItem :: Behavior (Maybe DI) -> UI Element
        dataItem _ = do
            liftIO (putStrLn "YES")
            entry1 <- UI.entry $ decide <$> bDB
            element entry1 # set style [("width", "300px")]
            return $ getElement  entry1

    -- GUI elements
    clrBtn      <- UI.button #+ [string "*"]
    xpdBtn      <- UI.button #+ [string "+"]
    lbSelection <- dataItem    bLBSelectionDI
    listBox     <- UI.listBox  bLBItems bLBSelection bDisplayDI
    element listBox # set (attr "size") "10" # set style [("width","300px")]

    let eLBSelection :: Event (Maybe DBKey)
        eLBSelection = rumors $ UI.userSelection listBox

    (eFillLB, hFillLB) <- liftIO newEvent

    let query :: String -> Maybe DBKey -> UI ()
        query url mk  = do
            liftIO $ putStrLn ("query " ++ show url ++ " " ++ show mk)
            (s,p,o) <- liftIO $ doRDFQuery2 endPoint mk bDB
            liftIO $ hFillLB (case spoType of SUB -> s; PRE -> p; OBJ -> o)
            return ()

        queryNothing :: () -> UI ()
        queryNothing _ = do
            query endPoint Nothing

    on UI.click clrBtn $ \_ -> do
        liftIO $ putStrLn "UI.click clrBtn"
        queryNothing ()

    onEvent eLBSelection $ \mk -> do
        liftIO $ putStrLn "onEvent eLBSelection"
        query endPoint mk

    let dbFill      :: [(String,BindingValue)] -> DB DI -> DB DI
        dbFill ss _ = foldr dbCreate  dbEmpty ss

    -- bDB :: Behavior (DB DI)
    bDB <- accumB dbEmpty $ concatenate <$> unions
        [ dbFill    <$> eFillLB
        ]

    -- selection
    -- bLBSelection :: Behavior (Maybe DBKey)
    bLBSelection <- stepper Nothing eLBSelection `hcDebug` "stepper"

    let bLookup :: Behavior (DBKey -> Maybe DI)
        bLookup = flip dbLookup <$> bDB `hcDebug` "bLookup"

        bDisplayDI :: Behavior (DBKey -> UI Element)
        bDisplayDI = (UI.string .) <$> (maybe "" showDI .) <$> bLookup

        bLBItems :: Behavior [DBKey]
        bLBItems = dbKeys <$> bDB

        bLBSelectionDI :: Behavior (Maybe DI)
        bLBSelectionDI = (=<<) <$> bLookup <*> bLBSelection `hcDebug` "bLBSelection"

    layout <- column [ element lbSelection
                     , row [ element clrBtn, element xpdBtn ]
                     , element listBox
                     ]

    return (lbSelection, layout, hFillLB)

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
dbLookup key   (DB _      db0) = Map.lookup                       key      db0 `hcDebug` ("dbLookup " ++ (show key))

------------------------------------------------------------------------------
-- What is stored in data base

type DI = (String, BindingValue)

showDI :: DI -> String
showDI (x,_) = x

------------------------------------------------------------------------------

doRDFQuery1 :: String -> String -> String -> String
              -> IO ( [(String, BindingValue)]
                    , [(String, BindingValue)]
                    , [(String, BindingValue)]
                    )
doRDFQuery1 url _ _ _ = ttt url

doRDFQuery2 :: String
              -> Maybe DBKey
              -> Behavior (DB DI)
              -> IO ( [(String, BindingValue)]
                    , [(String, BindingValue)]
                    , [(String, BindingValue)]
                    )
doRDFQuery2 url mk bdb = do
    putStrLn ""
    putStrLn url
    putStrLn ("MK : " ++ show mk)
    db0 <- currentValue bdb
    case mk of
        (Just k) -> do let v = dbLookup k db0
                       putStrLn ("DBValue: " ++ show v)
                       case v of
                           Just b -> do rrr <- ttwv url b
                                        print rrr
                                        return rrr
                           Nothing             -> ttt url
        _        -> do putStrLn "key is NOTHING"
                       ttt url
-- End of file.
