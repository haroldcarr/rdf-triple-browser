{-
Created       : 2014 Jul 17 (Thu) 08:38:10 by Harold Carr.
Last Modified : 2014 Aug 16 (Sat) 15:01:21 by Harold Carr.

- based on
  - http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
  - threepenny-gui MissingDollars sample
-}

-- TODO : expand/contract

{-# LANGUAGE RecursiveDo #-}

module RTB where

import qualified Data.Map                    as Map
import           Data.Maybe                  (fromJust)
import           Data.RDF.Types              (Node (..))
import qualified Data.Text                   as T (pack)
import           Database.HSparql.Connection
import           Debug.Trace
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core
import           RTBQ

hcDebug :: c -> String -> c
hcDebug = flip trace

defaultEndPoint :: String
defaultEndPoint = "http://localhost:3030/ds/query"

data SPOType = SUB | PRE | OBJ deriving Eq

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
                                   # set (attr "value") defaultEndPoint
    submitBtn         <- UI.button #+ [string "submit"]

    -- input and display elements and more
    (subLBSelection, subClrBtn, subLayout, eSubLBSelection, bSubDB, hSubFillLB) <- mkSPOPanel SUB
    (preLBSelection, preClrBtn, preLayout, ePreLBSelection, bPreDB, hPreFillLB) <- mkSPOPanel PRE
    (objLBSelection, objClrBtn, objLayout, eObjLBSelection, bObjDB, hObjFillLB) <- mkSPOPanel OBJ

    -- display elements
    frame    <- UI.frame # set (attr "name")   "top"
                         # set (attr "target") "top"
                         # set (attr "src")    "http://haroldcarr.com/"
    frameset <- UI.frameset #+ [ element frame ]

    -- action

    on UI.click submitBtn $ \_ -> do
        liftIO $ putStrLn "submit"
        query aTrueBoundNodePair aTrueBoundNodePair aTrueBoundNodePair
        return ()

    on UI.click subClrBtn   $ \_  -> do
        element subLBSelection # set value (show SUB);
        slt SUB Nothing True
    onEvent eSubLBSelection $ \mk ->
        slt SUB mk      False

    on UI.click preClrBtn   $ \_  -> do
        element preLBSelection # set value (show PRE);
        slt PRE Nothing True
    onEvent ePreLBSelection $ \mk ->
        slt PRE mk      False

    on UI.click objClrBtn   $ \_  -> do
        element objLBSelection # set value (show OBJ);
        slt OBJ Nothing True
    onEvent eObjLBSelection $ \mk ->
        slt OBJ mk      False

    let query :: (Bool, BindingValue) -> (Bool, BindingValue) -> (Bool, BindingValue) -> UI ()
        query s p o = do
            url <- get value sparqlEndpointURL;
            liftIO $ putStrLn ("query " ++ show url ++ " " ++ show s ++" " ++ show p ++ " " ++ show o)
            (s',p',o') <- liftIO $ query2 url s p o
            -- These are the MAGIC steps.  A Handler feeds events to its corresponding Event (from newEvent)
            mapM_ liftIO [hSubFillLB s', hPreFillLB p', hObjFillLB o']
            return ()

        sndLookup n db0 = (False, snd (fromJust $ dbLookup n db0))

        varOrSelection :: String -> DB DI -> (Bool, BindingValue)
        varOrSelection lbSel db0 =
            if lbSel == show SUB || lbSel == show PRE || lbSel == show OBJ
                then aTrueBoundNodePair
                else sndLookup 0 db0

        slt :: SPOType -> Maybe DBKey -> Bool -> UI ()
        slt spoType mk isClk = do
            liftIO $ putStrLn ("slt " ++ show spoType ++ " " ++ show mk)
            [sDB   , pDB   , oDB   ] <- mapM currentValue [bSubDB, bPreDB, bObjDB]
            [sLBSel, pLBSel, oLBSel] <- mapM (get value) [subLBSelection, preLBSelection, objLBSelection]
            case mk of
                Just k -> element frame # set (attr "src")
                                              (case spoType of
                                                    SUB -> fst (fromJust $ dbLookup k sDB)
                                                    PRE -> fst (fromJust $ dbLookup k pDB)
                                                    OBJ -> fst (fromJust $ dbLookup k oDB))
                _      -> element frame
            case mk of
                Just k -> case spoType of
                              SUB -> element subLBSelection # set value (fst (fromJust $ dbLookup k sDB))
                              PRE -> element preLBSelection # set value (fst (fromJust $ dbLookup k pDB))
                              OBJ -> element objLBSelection # set value (fst (fromJust $ dbLookup k oDB))
                _      -> element frame
            let tval     = aTrueBoundNodePair
                fval     = sndLookup (fromJust mk)
                pick spo lbSel db0 = if spoType == spo then
                                         if isClk then tval else fval db0
                                         else varOrSelection lbSel db0
            query (pick SUB sLBSel sDB)
                  (pick PRE pLBSel pDB)
                  (pick OBJ oLBSel oDB)

        aTrueBoundNodePair   :: (Bool, BindingValue)
        aTrueBoundNodePair   =  (True, aBoundNode)

    -- layout
    grid [ [ row [ element sparqlEndpointURL, element submitBtn ] ]
         , [ row [ element subLayout, element preLayout, element objLayout ] ]
         , [ element frameset ]
         ]

mkSPOPanel :: SPOType
           -> UI ( UI.Element -- current selection
                 , UI.Element -- clrBtn
                 , UI.Element -- layout
                 , Event (Maybe DBKey)
                 , Behavior (DB DI)
                 , Handler [(String, BindingValue)]
                 )
mkSPOPanel spoType = mdo
    -- GUI elements
    lbSelection <- UI.input  # set (attr "size") "40" # set (attr "type") "text"
                             # set value (show spoType)
    clrBtn      <- UI.button #+ [string "*"]
    xpdBtn      <- UI.button #+ [string "+"]
    listBox     <- UI.listBox  bLBItems bLBSelection bDisplayDI
    element listBox # set (attr "size") "10" # set style [("width","300px")]

    let eLBSelection :: Event (Maybe DBKey)
        eLBSelection = rumors $ UI.userSelection listBox

    (eFillLB, hFillLB) <- liftIO newEvent

    -- bDB :: Behavior (DB DI)
    bDB <- accumB dbEmpty $ dbFill <$> eFillLB

    -- bLBSelection :: Behavior (Maybe DBKey)
    bLBSelection <- stepper Nothing eLBSelection `hcDebug` "stepper"

    let dbFill         :: [(String,BindingValue)] -> DB DI -> DB DI
        dbFill ss _    = foldr dbCreate  dbEmpty ss

        bLookup        :: Behavior (DBKey -> Maybe DI)
        bLookup        = flip dbLookup <$> bDB `hcDebug` "bLookup"

        bDisplayDI     :: Behavior (DBKey -> UI Element)
        bDisplayDI     = (UI.string .) <$> (maybe "" showDI .) <$> bLookup

        bLBItems       :: Behavior [DBKey]
        bLBItems       = dbKeys <$> bDB

    layout <- column [ element lbSelection
                     , row [ element clrBtn, element xpdBtn ]
                     , element listBox
                     ]

    return (lbSelection, clrBtn, layout, eLBSelection, bDB, hFillLB)

aBoundNode :: BindingValue
aBoundNode =  Bound (UNode (T.pack "A DUMMY NODE"))

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
dbLookup key   (DB _      db0) =                 Map.lookup    key   db0 `hcDebug` ("dbLookup " ++ show key)

------------------------------------------------------------------------------
-- What is stored in data base

type DI = (String, BindingValue)

showDI :: DI -> String
showDI (x,_) = x

-- End of file.
