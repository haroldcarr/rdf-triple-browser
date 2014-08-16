{-
Created       : 2014 Jul 17 (Thu) 08:38:10 by Harold Carr.
Last Modified : 2014 Aug 16 (Sat) 09:53:19 by Harold Carr.

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
                                   # set (attr "value") endPoint
    submitBtn         <- UI.button #+ [string "submit"]

    -- input and display elements and more
    (subClrBtn, subLayout, eSubLBSelection, bSubDB, hSubFillLB) <- mkSPOPanel SUB
    (preClrBtn, preLayout, ePreLBSelection, bPreDB, hPreFillLB) <- mkSPOPanel PRE
    (objClrBtn, objLayout, eObjLBSelection, bObjDB, hObjFillLB) <- mkSPOPanel OBJ

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

    on UI.click subClrBtn   $ \_  -> slt SUB Nothing True
    onEvent eSubLBSelection $ \mk -> slt SUB mk      False

    on UI.click preClrBtn   $ \_  -> slt PRE Nothing True
    onEvent ePreLBSelection $ \mk -> slt PRE mk      False

    on UI.click objClrBtn   $ \_  -> slt OBJ Nothing True
    onEvent eObjLBSelection $ \mk -> slt OBJ mk      False

    let query :: (Bool, BindingValue) -> (Bool, BindingValue) -> (Bool, BindingValue) -> UI ()
        query s p o = do
            url <- get value sparqlEndpointURL;
            liftIO $ putStrLn ("query " ++ show url ++ " " ++ show s ++" " ++ show p ++ " " ++ show o)
            (s',p',o') <- liftIO $ test'' url s p o
            -- These are the MAGIC steps.  A Handler feeds events to its corresponding Event (from newEvent)
            mapM_ liftIO [hSubFillLB s', hPreFillLB p', hObjFillLB o']
            return ()

        sndLookup n db0 = (False, snd (fromJust $ dbLookup n db0))

        varOrSelection :: DB DI -> (Bool, BindingValue)
        varOrSelection db0 =
            if dbSize db0 == 1
                then sndLookup 0 db0
                else aTrueBoundNodePair

        slt :: SPOType -> Maybe DBKey -> Bool -> UI ()
        slt spoType mk isClk = do
            liftIO $ putStrLn ("slt " ++ show spoType ++ " " ++ show mk)
            s <- currentValue bSubDB
            p <- currentValue bPreDB
            o <- currentValue bObjDB
            case mk of
                Just k -> element frame # set (attr "src")
                                              (case spoType of
                                                    SUB -> fst (fromJust $ dbLookup k s)
                                                    PRE -> fst (fromJust $ dbLookup k p)
                                                    OBJ -> fst (fromJust $ dbLookup k o))
                _      -> element frame
            let tval     = aTrueBoundNodePair
                fval     = sndLookup (fromJust mk)
                pick b x = if b then
                                if isClk then tval else fval x
                                else varOrSelection x
            query (pick (spoType == SUB) s)
                  (pick (spoType == PRE) p)
                  (pick (spoType == OBJ) o)

        aTrueBoundNodePair   :: (Bool, BindingValue)
        aTrueBoundNodePair   =  (True, aBoundNode)

    -- layout
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
    -- NOTE: if you move the declarations in this let to the one with many below
    --       then it compiles and starts up, but nothing displays
    let decide :: DB DI -> String
        decide db0 | dbSize db0 > 1 = show spoType
                   | otherwise      = fst $ fromMaybe aStringBoundNodePair (dbLookup 0 db0) `hcDebug` ("decide " ++ show spoType)

        dataItem :: Behavior (Maybe DI) -> UI Element
        dataItem _ = do
            liftIO (putStrLn "dataItem")
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

        bLBSelectionDI :: Behavior (Maybe DI)
        bLBSelectionDI = (=<<) <$> bLookup <*> bLBSelection `hcDebug` "bLBSelection"

        aStringBoundNodePair :: (String, BindingValue)
        aStringBoundNodePair =  ("NOT SUPPOSED TO HAPPEN", aBoundNode)

    layout <- column [ element lbSelection
                     , row [ element clrBtn, element xpdBtn ]
                     , element listBox
                     ]

    return (clrBtn, layout, eLBSelection, bDB, hFillLB)

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
