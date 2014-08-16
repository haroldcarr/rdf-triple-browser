{-
Created       : 2014 Jul 17 (Thu) 08:38:10 by Harold Carr.
Last Modified : 2014 Aug 15 (Fri) 18:41:47 by Harold Carr.

- based on
  - http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
  - threepenny-gui MissingDollars sample
-}

{-# LANGUAGE RecursiveDo #-}

module RTB where

import qualified Data.Map                    as Map
import           Data.Maybe                  (fromJust, fromMaybe)
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

badNode                = UNode (T.pack "BAD")
badBoundNode           = Bound badNode
badStringBoundNodePair = ("NOT SUPPOSED TO HAPPEN", badBoundNode)

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
    (subClrBtn, subLayout, eSubLBSelection, bSubDB, hSubFillLB) <- mkSPOPanel SUB
    (preClrBtn, preLayout, ePreLBSelection, bPreDB, hPreFillLB) <- mkSPOPanel PRE
    (objClrBtn, objLayout, eObjLBSelection, bObjDB, hObjFillLB) <- mkSPOPanel OBJ

    let query :: String -> (Bool, BindingValue) -> (Bool, BindingValue) -> (Bool, BindingValue) -> UI ()
        query url s p o = do
            liftIO $ putStrLn ("query " ++ show url ++ " " ++ show s ++" " ++ show p ++ " " ++ show o)
            (s',p',o') <- liftIO $ test'' endPoint s p o
            liftIO $ hSubFillLB s'
            liftIO $ hPreFillLB p'
            liftIO $ hObjFillLB o'
            return ()

        whatever :: DB DI -> (Bool, BindingValue)
        whatever db0 =
            if (dbSize db0) == 1
                then (False, snd (fromJust $ dbLookup 0 db0))
                else (True , badBoundNode)

    on UI.click subClrBtn $ \_ -> do
        liftIO $ putStrLn "UI.click subClrBtn"
        p <- currentValue bPreDB
        o <- currentValue bObjDB
        query endPoint
              (True, badBoundNode)
              (whatever p)
              (whatever o)

    onEvent eSubLBSelection $ \mk -> do
        liftIO $ putStrLn ("onEvent eSubLBSelection: " ++ (show mk))
        s <- currentValue bSubDB
        p <- currentValue bPreDB
        o <- currentValue bObjDB
        query endPoint
              (False, snd (fromJust $ dbLookup (fromJust mk) s))
              (whatever p)
              (whatever o)

    on UI.click preClrBtn $ \_ -> do
        liftIO $ putStrLn "UI.click preClrBtn"
        s <- currentValue bSubDB
        o <- currentValue bObjDB
        query endPoint
              (whatever s)
              (True, badBoundNode)
              (whatever o)

    onEvent ePreLBSelection $ \mk -> do
        liftIO $ putStrLn ("onEvent ePreLBSelection: " ++ (show mk))
        s <- currentValue bSubDB
        p <- currentValue bPreDB
        o <- currentValue bObjDB
        query endPoint
              (whatever s)
              (False, snd (fromJust $ dbLookup (fromJust mk) p))
              (whatever o)

    on UI.click objClrBtn $ \_ -> do
        liftIO $ putStrLn "UI.click objClrBtn"
        s <- currentValue bSubDB
        p <- currentValue bPreDB
        query endPoint
              (whatever s)
              (whatever p)
              (True, badBoundNode)

    onEvent eObjLBSelection $ \mk -> do
        liftIO $ putStrLn ("onEvent eObjLBSelection: " ++ (show mk))
        s <- currentValue bSubDB
        p <- currentValue bPreDB
        o <- currentValue bObjDB
        query endPoint
              (whatever s)
              (whatever p)
              (False, snd (fromJust $ dbLookup (fromJust mk) o))

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
           -> UI ( UI.Element -- clrBtn
                 , UI.Element -- layout
                 , Event (Maybe DBKey)
                 , Behavior (DB DI)
                 , Handler [(String, BindingValue)]
                 )
mkSPOPanel spoType = mdo
    let decide :: DB DI -> String
        decide db0 | dbSize db0 > 1 = show spoType
                   | otherwise      = fst $ fromMaybe badStringBoundNodePair (dbLookup 0 db0) `hcDebug` ("decide " ++ (show spoType))

        dataItem :: Behavior (Maybe DI) -> UI Element
        dataItem _ = do
            entry1 <- UI.entry $ decide <$> bDB
            liftIO (putStrLn "dataItem")
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

    return (clrBtn, layout, eLBSelection, bDB, hFillLB)

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
                           Just (_, Bound b) -> do rrr <- test' url (False, b) (True, badNode) (True, badNode)
                                                   print rrr
                                                   return rrr
                           Nothing           -> ttt url
        _        -> do putStrLn "key is NOTHING"
                       ttt url
-- End of file.
